package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.utils.*;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;
import net.pieroxy.conkw.utils.hashing.HashTools;
import net.pieroxy.conkw.webapp.model.NeedsAuthResponse;
import net.pieroxy.conkw.webapp.model.Response;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.ApiAuthManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;

public class ExternalInstanceGrabber extends AsyncGrabber<SimpleCollector, ExternalInstanceGrabber.ExternalInstanceGrabberConfig> {
  private String sessionToken;

  @Override
  public boolean changed(SimpleCollector c) {
    return true;
  }

  @Override
  public SimpleCollector getDefaultCollector() {
    return new SimpleTransientCollector(this, DEFAULT_CONFIG_KEY);
  }

  @Override
  public ExternalInstanceGrabberConfig getDefaultConfig() {
    return new ExternalInstanceGrabberConfig();
  }

  @Override
  public void grabSync(SimpleCollector c) {
    URL url = null;
    try {
      url = new URL(getConfig().getUrl() + (StringUtil.isNullOrEmptyTrimmed(sessionToken)?"":("&"+ApiAuthManager.SID_FIELD+"="+sessionToken)));
      URLConnection con = url.openConnection();
      con.setConnectTimeout(500);
      con.setReadTimeout(500);
      long ms = System.currentTimeMillis();
      HttpURLConnection http = (HttpURLConnection) con;
      http.connect();

      InputStream is;
      if (canLogFine()) {
        is = DebugTools.debugHttpRequest(http);
      } else {
        is = http.getInputStream();
      }


      try (Response data = JsonHelper.getJson().deserialize(Response.class, is)) {
        if (canLogFine()) {
          log(Level.FINE, "Time to grab: " + (System.currentTimeMillis()-ms) + "ms");
        }
        try (ResponseData res = new ResponseData(this, data.getTimestamp(), data.getNumCount(), data.getStrCount())) {
          if (data.isNeedsAuthentication()) {
            try {
              authenticate();
            } catch (DisplayMessageException e) {
              res.addError(e.getMessage());
              c.copyDataFrom(res);
            }
            return;
          }
          data.getMetrics().entrySet().forEach(e -> {
            ResponseData d = e.getValue();
            String prefix = e.getKey() + "_";
            d.getNum().entrySet().forEach(de -> res.addMetric(prefix + de.getKey(), de.getValue()));
            d.getStr().entrySet().forEach(de -> res.addMetric(prefix + de.getKey(), de.getValue()));
            d.getErrors().forEach(de -> res.addError(prefix + de));
          });
          c.copyDataFrom(res);
        }
      }
    } catch (Exception e) {
      log(Level.SEVERE, "EIG Grabbing " + getName() + " with URL " + url, e);
    }
  }

  private void authenticate() throws Exception {
    if (getConfig().getLogin() == null || getConfig().getPassword()==null) throw new DisplayMessageException(this.getClass().getSimpleName() + "/" + getName() + ": Authentication is not configured but endpoint needs it.");
    NeedsAuthResponse res1 = HttpUtils.get(getConfig().getUrl() + "&" + ApiAuthManager.USER_FIELD + "=" + URLEncoder.encode(getConfig().getLogin(), "UTF-8"), NeedsAuthResponse.class);
    if (StringUtil.isNullOrEmptyTrimmed(res1.getSaltForPassword())) throw new DisplayMessageException(this.getClass().getSimpleName() + "/" + getName() + ": Authentication failed at step 1.");
    String pwd = HashTools.toSHA1(res1.getSaltForPassword() + getConfig().getPassword());
    NeedsAuthResponse res = HttpUtils.get(getConfig().getUrl() + "&" + ApiAuthManager.USER_FIELD + "=" + URLEncoder.encode(getConfig().getLogin(), "UTF-8") + "&" + ApiAuthManager.PASS_FIELD + "=" + pwd, NeedsAuthResponse.class);
    sessionToken = res.getSessionToken();
    if (StringUtil.isNullOrEmptyTrimmed(sessionToken)) throw new DisplayMessageException(this.getClass().getSimpleName() + "/" + getName() + ": Authentication failed.");
  }

  @Override
  public String getDefaultName() {
    return "ext";
  }

  public static class ExternalInstanceGrabberConfig {
    private String url;
    private String login;
    private String password;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getLogin() {
      return login;
    }

    public void setLogin(String login) {
      this.login = login;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }
}
