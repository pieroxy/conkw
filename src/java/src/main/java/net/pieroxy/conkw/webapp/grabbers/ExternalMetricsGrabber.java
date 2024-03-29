package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.Emi;

import java.io.*;
import java.time.Duration;
import java.util.Date;
import java.util.logging.Level;

public class ExternalMetricsGrabber extends Grabber<SimpleCollector, ExternalMetricsGrabber.ExternalMetricsGrabberConfig> {

  private ResponseData globalData = null;
  private boolean changed;
  private Thread saveThread;
  private long lastCollection;

  @Override
  public synchronized void collect(SimpleCollector c) {
    if (System.currentTimeMillis() - lastCollection < 900) {
      // Last collection was less than 1s ago.
      // Testing with 900ms to avoid threshold effects.
      return;
    }
    lastCollection = System.currentTimeMillis();
    c.copyDataFrom(globalData);
    c.collectionDone();
  }

  @Override
  public void dispose() {
    saveThread = null;
    synchronized (this) {
      this.notify();
    }
  }

  @Override
  public String getDefaultName() {
    return "emi";
  }

  @Override
  public SimpleCollector getDefaultCollector(boolean includeAccumulatorIfAny) {
    return new SimpleCollector(this, DEFAULT_CONFIG_KEY, includeAccumulatorIfAny ? getDefaultAccumulator() : null);
  }

  public void addMetric(String name, double value) {
    globalData.atomicAddMetricWithTimestamp(name, value);
    save();
  }

  public void addMetric(String name, String value) {
    globalData.atomicAddMetricWithTimestamp(name, value);
    save();
  }

  private void save() {
    changed = true;
    if (saveThread == null) (saveThread = new Thread(() -> saveData())).start();
  }

  private File getStorageFile() {
    return new File(getStorage(), getClass().getSimpleName() + "." + getName()+".json");
  }

  @SuppressWarnings("deprecation")
  private void saveData() {
    int hour=-1; // Purge old data on server start
    while (saveThread == Thread.currentThread()) {
      try {
        synchronized (this) {
          this.wait(10000); // No need to save more than once every 10s
        }
        ResponseData data = globalData;
        {
          // Purging expired values every hours at most
          int hoursnow=new Date().getHours();
          if (hoursnow != hour) {
            hour = hoursnow;
            data.purgeDataOlderThan(Duration.ofDays(30));
          }
        }
        if (changed) {
          JsonWriter w = JsonHelper.getWriter();
          DslJson<Object> json = JsonHelper.getJson();
          if (canLogFine()) log(Level.FINE, "Saving state " + getName());
          synchronized (w) {
            try (OutputStream os = new FileOutputStream(getStorageFile())) {
              w.reset(os);
              json.serialize(w, data);
              w.flush();
              changed = false;
            } catch (Exception e) {
              log(Level.SEVERE, "Saving " + getName(), e);
            }
          }
        }
      } catch (Exception e) {
        log(Level.SEVERE, getName(), e);
      }
    }
    log(Level.INFO, "Stopping save thread.");
  }


  @Override
  public void initializeGrabber(File homeDir) {
    super.initializeGrabber(homeDir);
    Emi.addOrUpdateGrabber(this);

    try (FileInputStream fis = new FileInputStream(getStorageFile())) {
      log(Level.INFO, "Loading state from " + getStorageFile().getAbsolutePath());
      globalData = JsonHelper.getJson().deserialize(ResponseData.class, fis);
    } catch (FileNotFoundException e) {
      log(Level.WARNING, "Could not load cached data file: " + getStorageFile());
      globalData = new ResponseData(this, System.currentTimeMillis(), HashMapPool.SIZE_UNKNOWN, HashMapPool.SIZE_UNKNOWN);
    } catch (Exception e) {
      log(Level.SEVERE, "Could not load cached data file.", e);
      globalData = new ResponseData(this, System.currentTimeMillis(), HashMapPool.SIZE_UNKNOWN, HashMapPool.SIZE_UNKNOWN);
    }
  }

  public static class ExternalMetricsGrabberConfig {
  }
}
