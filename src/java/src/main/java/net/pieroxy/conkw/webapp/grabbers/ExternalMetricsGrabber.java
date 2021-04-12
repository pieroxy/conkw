package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.Emi;

import java.io.*;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

public class ExternalMetricsGrabber extends Grabber {

  private ResponseData data = null;
  private boolean changed;
  private Thread saveThread;

  @Override
  public ResponseData grab() {
    return data;
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


  public void addMetric(String name, double value) {
    data.atomicAddMetricWithTimestamp(name, value);
    save();
  }

  public void addMetric(String name, String value) {
    data.atomicAddMetricWithTimestamp(name, value);
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
  protected void setConfig(Map<String, String> config) {
    Emi.addOrUpdateGrabber(this);

    try (FileInputStream fis = new FileInputStream(getStorageFile())) {
      log(Level.INFO, "Loading state from " + getStorageFile().getAbsolutePath());
      data = JsonHelper.getJson().deserialize(ResponseData.class, fis);
    } catch (FileNotFoundException e) {
      log(Level.WARNING, "Could not load cached data file: " + getStorageFile());
    } catch (Exception e) {
      log(Level.SEVERE, "Could not load cached data file.", e);
      data = new ResponseData(this, System.currentTimeMillis());
    }
  }
}
