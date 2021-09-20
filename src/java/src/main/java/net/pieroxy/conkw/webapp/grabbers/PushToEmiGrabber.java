package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.collectors.Collector;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimplePermanentCollector;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.grabbersBase.GrabberListener;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.model.EmiInput;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.Emi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PushToEmiGrabber extends Grabber<SimpleCollector> implements GrabberListener, Runnable {

  private String prefix;
  private URL url;
  private Thread runner;
  private List<Grabber> grabbers;

  @Override
  public void collect(SimpleCollector c) {

  }

  @Override
  public void dispose() {
    log(Level.INFO, "Stopping PushToEmiGrabber");
    Thread old = runner;
    runner = null;
    synchronized (old) {
      old.notifyAll();
    }
  }

  @Override
  public String getDefaultName() {
    return null;
  }

  @Override
  public SimpleCollector getDefaultCollector() {
    return new SimplePermanentCollector(this);
  }

  @Override
  protected void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
    try {
      url = new URL(config.get("url"));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    prefix = config.get("prefix");
    runner = new Thread(this, "PushToEmiGrabber");
    runner.start();
  }

  @Override
  public void setGrabberList(List<Grabber> grabbers) {
    this.grabbers = grabbers;
  }

  @Override
  public void run() {
    while (true) {
      Thread t = runner;
      if (runner == null) break;
      try {
        synchronized (t) {
          t.wait(getSleepTime());
        }
        if (runner == null) break;
        if (grabbers == null || grabbers.isEmpty()) {
          log(Level.WARNING, "Grabbers not yet initialized");
          continue;
        }
        if (runner == null) break;

        EmiInput data = grabData();
        if (runner == null) break;

        sendData(data);

      } catch (Exception e) {
        log(Level.SEVERE, "", e);
      }
    }
  }

  private EmiInput grabData() {
    EmiInput input = new EmiInput();
    Map<String, String> str = new HashMap<>();
    Map<String, Double> num = new HashMap<>();
    input.setNum(num);
    input.setStr(str);
    grabbers.forEach(g -> {
      if (shouldExtract(g.getName())) {
        ResponseData data = grab(g); // Static grab cannot be parametrized
        if (data!=null) {
          data.getNum().forEach((k, v) -> num.put(prefix+"_"+g.getName() + "_" + k, v));
          data.getStr().forEach((k, v) -> str.put(prefix+"_"+g.getName() + "_" + k, v));
          data.getErrors().forEach(s -> log(Level.WARNING, s));
        }
      }
    });
    return input;
  }

  private ResponseData grab(Grabber g) {
    Collector c = g.getDefaultCollector();
    g.collect(c);
    return c.getData();
  }

  private void sendData(EmiInput input) throws IOException {
    try {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(200);
      conn.setReadTimeout(200);
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", Emi.CONTENT_TYPE);
      conn.setDoOutput(true);
      try (OutputStream os = conn.getOutputStream()) {
        DslJson<Object> json = JsonHelper.getJson();
        JsonWriter w = JsonHelper.getWriter();
        synchronized (w) {
          w.reset(os);
          json.serialize(w, input);
          w.flush();
        }
        int rc = conn.getResponseCode();
        if (rc != 200) {
          String text = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
          log(Level.WARNING, "Emi responded with code " + rc + "\n" + text);
        }
      }
    } catch (SocketTimeoutException e) {
      log(Level.SEVERE, "Timed out sending data to " + url);
    } catch (Exception e) {
      log(Level.SEVERE, "Could not send data to " + url, e);
    }
  }

  private long getSleepTime() {
    return (long)(Math.random()*20-10+1000);
  }
}
