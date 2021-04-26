package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PushToEmiGrabber extends Grabber implements GrabberListener, Runnable {

  private String prefix;
  private URL url;
  private Thread runner;
  private List<Grabber> grabbers;

  @Override
  public ResponseData grab() {
    return null;
  }

  @Override
  public void dispose() {
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
  protected void setConfig(Map<String, String> config) {
    try {
      url = new URL(config.get("url"));
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    prefix = config.get("prefix");
    runner = new Thread(this);
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
      if (t == null) break;
      try {
        synchronized (t) {
          t.wait(getSleepTime());
        }
        if (grabbers == null || grabbers.isEmpty()) {
          log(Level.WARNING, "Grabbers not yet initialized");
          continue;
        }

        EmiInput data = grabData();

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
        ResponseData data = g.grab();
        if (data!=null) {
          data.getNum().forEach((k, v) -> num.put(prefix+"_"+g.getName() + "_" + k, v));
          data.getStr().forEach((k, v) -> str.put(prefix+"_"+g.getName() + "_" + k, v));
          data.getErrors().forEach(s -> log(Level.WARNING, s));
        }
      }
    });
    return input;
  }

  private void sendData(EmiInput input) throws IOException {
    try {
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
    } catch (Exception e) {
      log(Level.SEVERE, "Could not send data to " + url, e);
    }
  }

  private long getSleepTime() {
    return (long)(Math.random()*20-10+1000);
  }
}
