package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.grabbersBase.GrabberListener;
import net.pieroxy.conkw.grabbersBase.PartiallyExtractableConfig;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.model.EmiInput;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.Emi;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PushToEmiGrabber extends Grabber<SimpleCollector, PushToEmiGrabber.PushToEmiGrabberConfig> implements GrabberListener, Runnable {

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
    while (old.isAlive()) {
      try{ Thread.sleep(10); } catch (Exception e) {}
    }
  }

  @Override
  public String getDefaultName() {
    return null;
  }

  @Override
  public SimpleCollector getDefaultCollector() {
    return new SimpleCollector(this, DEFAULT_CONFIG_KEY, getDefaultAccumulator());
  }

  @Override
  public PushToEmiGrabberConfig getDefaultConfig() {
    return new PushToEmiGrabberConfig();
  }

  @Override
  public void initializeGrabber(File homeDir) {
    super.initializeGrabber(homeDir);
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

        try (EmiInput data = grabData()) {
          if (runner == null) break;

          sendData(data);
          if (canLogFine()) log(Level.FINE, "Sent " + data.getNum().size());
        }

      } catch (Exception e) {
        log(Level.SEVERE, "", e);
      }
    }
    log(Level.INFO, "Shutting down");
  }

  private EmiInput grabData() {
    EmiInput input = new EmiInput();
    Map<String, String> str = HashMapPool.getInstance().borrow(HashMapPool.getUniqueCode(this.getClass().getName(), getName(), "str"));
    Map<String, Double> num = HashMapPool.getInstance().borrow(HashMapPool.getUniqueCode(this.getClass().getName(), getName(), "num"));
    input.setNum(num);
    input.setStr(str);
    grabbers.forEach(g -> {
      if (shouldExtract(g.getName())) {
        try (ResponseData data = grab(g)){ // Static grab cannot be parametrized for now
          if (canLogFine()) log(Level.FINE, "Grabbed " + data.getNum().size());

          if (data != null) {
            data.getNum().forEach((k, v) -> num.put(getConfig().getPrefix() + "_" + g.getName() + "_" + k, v));
            data.getStr().forEach((k, v) -> str.put(getConfig().getPrefix() + "_" + g.getName() + "_" + k, v));
            data.getErrors().forEach(s -> log(Level.WARNING, s));
          }
        }
      }
    });
    return input;
  }

  private ResponseData grab(Grabber g) {
    g.addActiveCollector(Grabber.DEFAULT_CONFIG_KEY);
    g.collect();
    if (canLogFine()) log(Level.FINE, g.getCollectorToUse(Grabber.DEFAULT_CONFIG_KEY).toString());
    return g.getCollectorToUse(Grabber.DEFAULT_CONFIG_KEY).getDataCopy();
  }

  private void sendData(EmiInput input) throws IOException {
    try {
      HttpURLConnection conn = (HttpURLConnection) getConfig().getUrl().openConnection();
      conn.setConnectTimeout(getConfig().getTimeout());
      conn.setReadTimeout(getConfig().getTimeout());
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", Emi.CONTENT_TYPE_JSON);
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
      log(Level.SEVERE, "Timed out sending data to " + getConfig().getUrl());
    } catch (Exception e) {
      log(Level.SEVERE, "Could not send data to " + getConfig().getUrl(), e);
    }
  }

  private long getSleepTime() {
    return (long)(Math.random()*20-10+1000);
  }

  public static class PushToEmiGrabberConfig extends PartiallyExtractableConfig {
    private String prefix;
    private URL url;
    private int timeout = 200;

    public String getPrefix() {
      return prefix;
    }

    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }

    public URL getUrl() {
      return url;
    }

    public void setUrl(URL url) {
      this.url = url;
    }

    public int getTimeout() {
      return timeout;
    }

    public void setTimeout(int timeout) {
      this.timeout = timeout;
    }
  }
}
