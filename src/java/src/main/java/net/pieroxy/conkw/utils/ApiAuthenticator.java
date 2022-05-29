package net.pieroxy.conkw.utils;

import net.pieroxy.conkw.config.Credentials;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;
import net.pieroxy.conkw.utils.hashing.HashTools;
import net.pieroxy.conkw.webapp.model.NeedsAuthResponse;
import net.pieroxy.conkw.webapp.servlets.ApiAuthManager;

import java.net.URLEncoder;

public class ApiAuthenticator {

    public static String authenticate(Credentials credentials, String endpoint, Grabber grabber) throws Exception {
        if (credentials == null || credentials.getId() == null || credentials.getSecret()==null) {
            throw new DisplayMessageException(grabber.getGrabberFQN() + ": Authentication is not configured but endpoint needs it.");
        }
        NeedsAuthResponse res1 = HttpUtils.get(endpoint + "&" + ApiAuthManager.USER_FIELD + "=" + URLEncoder.encode(credentials.getId(), "UTF-8"), NeedsAuthResponse.class);
        if (StringUtil.isNullOrEmptyTrimmed(res1.getSaltForPassword())) {
            throw new DisplayMessageException(grabber.getGrabberFQN() + ": Authentication failed at step 1.");
        }
        String pwd = HashTools.toSHA1(res1.getSaltForPassword() + credentials.getSecret());
        NeedsAuthResponse res = HttpUtils.get(endpoint + "&" + ApiAuthManager.USER_FIELD + "=" + URLEncoder.encode(credentials.getId(), "UTF-8") + "&" + ApiAuthManager.PASS_FIELD + "=" + pwd, NeedsAuthResponse.class);
        String sessionToken = res.getSessionToken();
        if (StringUtil.isNullOrEmptyTrimmed(sessionToken)) {
            throw new DisplayMessageException(grabber.getGrabberFQN() + ": Authentication failed.");
        }
        return sessionToken;
    }

}
