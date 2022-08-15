package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.config.Credentials;
import net.pieroxy.conkw.config.CredentialsProvider;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.utils.ApiAuthenticator;
import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;
import net.pieroxy.conkw.webapp.model.MetricsApiResponse;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.ApiAuthManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

public class ExternalInstanceGrabber extends AsyncGrabber<SimpleCollector, ExternalInstanceGrabber.ExternalInstanceGrabberConfig> {
  private String sessionToken;

  @Override
  public boolean changed(SimpleCollector c) {
    return true;
  }

  @Override
  public SimpleCollector getDefaultCollector() {
    return new SimpleCollector(this, DEFAULT_CONFIG_KEY);
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


      try (MetricsApiResponse data = JsonHelper.getJson().deserialize(MetricsApiResponse.class, is)) {
        if (canLogFine()) {
          log(Level.FINE, "Time to grab: " + (System.currentTimeMillis()-ms) + "ms");
        }
        try (ResponseData res = new ResponseData(this, data.getTimestamp(), data.getNumCount(), data.getStrCount())) {
          if (data.isNeedsAuthentication()) {
            try {
              sessionToken = ApiAuthenticator.authenticate(getCredentials(getConfig()), getConfig().getUrl(), this);
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


  @Override
  public String getDefaultName() {
    return "ext";
  }

  public static class ExternalInstanceGrabberConfig implements CredentialsProvider {
    private String url;
    private Credentials credentials;
    private String credentialsRef;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    @Override
    public Credentials getCredentials() {
      return credentials;
    }

    @Override
    public String getCredentialsRef() {
      return credentialsRef;
    }
  }
}
