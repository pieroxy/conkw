package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.*;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;
import net.pieroxy.conkw.webapp.model.NeedsAuthResponse;
import net.pieroxy.conkw.webapp.model.Response;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.ApiAuthManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;

public class ExternalInstanceGrabber extends AsyncGrabber {

  private String targetUrl;
  private String login;
  private String pass;
  private String sessionToken;

  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public ResponseData grabSync() {
    try {
      URL url = new URL(targetUrl + (StringUtil.isNullOrEmptyTrimmed(sessionToken)?"":("&"+ApiAuthManager.SID_FIELD+"="+sessionToken)));
      URLConnection con = url.openConnection();
      HttpURLConnection http = (HttpURLConnection) con;
      http.connect();

      InputStream is;
      if (canLogFine()) {
        is = DebugTools.debugHttpRequest(http);
      } else {
        is = http.getInputStream();
      }

      Response data = JsonHelper.getJson().deserialize(Response.class, is);
      ResponseData res = new ResponseData(this, data.getTimestamp());
      if (data.isNeedsAuthentication()) {
        try {
          authenticate();
        } catch (DisplayMessageException e) {
          res.addError(e.getMessage());
          return res;
        }
        return grabSync();
      }
      data.getMetrics().entrySet().forEach(e -> {
        ResponseData d = e.getValue();
        String prefix = e.getKey() + "_";
        d.getNum().entrySet().forEach(de -> res.addMetric(prefix+de.getKey(), de.getValue()));
        d.getStr().entrySet().forEach(de -> res.addMetric(prefix+de.getKey(), de.getValue()));
        d.getErrors().forEach(de -> res.addError(prefix + de));
      });
      return res;
    } catch (Exception e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
    }
    return new ResponseData(this, System.currentTimeMillis());
  }

  private void authenticate() throws Exception {
    if (login == null || pass==null) throw new DisplayMessageException(this.getClass().getSimpleName() + "/" + getName() + ": Authentication is not configured but endpoint needs it.");
    NeedsAuthResponse res1 = HttpUtils.get(targetUrl + "&" + ApiAuthManager.USER_FIELD + "=" + URLEncoder.encode(login, "UTF-8"), NeedsAuthResponse.class);
    if (StringUtil.isNullOrEmptyTrimmed(res1.getSaltForPassword())) throw new DisplayMessageException(this.getClass().getSimpleName() + "/" + getName() + ": Authentication failed at step 1.");
    String pwd = HashTools.toSHA1(res1.getSaltForPassword() + pass);
    NeedsAuthResponse res = HttpUtils.get(targetUrl + "&" + ApiAuthManager.USER_FIELD + "=" + URLEncoder.encode(login, "UTF-8") + "&" + ApiAuthManager.PASS_FIELD + "=" + pwd, NeedsAuthResponse.class);
    sessionToken = res.getSessionToken();
    if (StringUtil.isNullOrEmptyTrimmed(sessionToken)) throw new DisplayMessageException(this.getClass().getSimpleName() + "/" + getName() + ": Authentication failed.");
  }

  @Override
  public String getDefaultName() {
    return "ext";
  }

  @Override
  protected void setConfig(Map<String, String> config) {
    targetUrl = config.get("url");
    if (targetUrl.indexOf('?')==-1) targetUrl+="?";
    login = config.get("login");
    pass = config.get("password");
  }
}
