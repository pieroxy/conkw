package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.grabbers.openweathermap.OneCallResponse;
import net.pieroxy.conkw.webapp.model.Response;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.logging.Level;

public class ExternalInstanceGrabber extends AsyncGrabber {

  private String targetUrl;

  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public ResponseData grabSync() {
    try {
      URL url = new URL(targetUrl);
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

  @Override
  public String getDefaultName() {
    return "ext";
  }

  @Override
  protected void setConfig(Map<String, String> config) {
    targetUrl = config.get("url");
  }
}
