package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.logging.Level;

public abstract class TimeThrottledGrabber extends AsyncGrabber {

  protected abstract Duration getTtl();
  protected abstract void load(ResponseData res);
  protected abstract String getCacheKey();

  private long lastRun=-1;
  private File storage;

  File getCacheFile() {
    if (storage==null) {
      storage = new File(getStorage(), getName()+".cache.json");
    }
    return storage;
  }

  ResponseData loadCacheFile() {
    File cacheFile = getCacheFile();
    if (!cacheFile.exists()) {
      log(Level.INFO, "Cache file not found, might be the first run.");
      return null;
    }
    try (FileInputStream fis = new FileInputStream(cacheFile)) {
      CachedData data = JsonHelper.getJson().deserialize(CachedData.class, fis);
      if (data.getCacheKey().equals(getCacheKey())) {
        return data.getData();
      } else {
        log(Level.INFO, "Configuration has changed, discarding cached data.");
      }
    } catch (Exception e) {
      log(Level.SEVERE, "Could not load cached data file.", e);
    }
    return null;
  }

  void writeCacheFile(ResponseData data) {
    CachedData cdata = new CachedData();
    cdata.setCacheKey(getCacheKey());
    cdata.setData(data);
    JsonWriter w = JsonHelper.getWriter();
    DslJson<Object> json = JsonHelper.getJson();
    synchronized (w) {
      try (OutputStream os = new FileOutputStream(getCacheFile())) {
        w.reset(os);
        json.serialize(w, cdata);
        w.flush();
      } catch (Exception e) {
        log(Level.SEVERE, e.getMessage());
      }
    }
  }

  @Override
  public boolean changed() {
    return System.currentTimeMillis() - lastRun > getTtl().toMillis();
  }

  @Override
  public ResponseData grabSync() {
    if (lastRun==-1) {
      ResponseData data = loadCacheFile();
      if (data!=null && data.getErrors().isEmpty()) {
        log(Level.INFO, "Loading from cache " + getCacheFile().getAbsolutePath());
        lastRun = data.getTimestamp();
        return data;
      }
    }
    try {
      ResponseData r = new ResponseData(this, System.currentTimeMillis());
      load(r);
      lastRun = System.currentTimeMillis();
      writeCacheFile(r);
      return r;
    } catch (Exception e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
      return new ResponseData(this, System.currentTimeMillis());
    }
  }

  public static class CachedData {
    private ResponseData data;
    private String cacheKey;

    public ResponseData getData() {
      return data;
    }

    public void setData(ResponseData data) {
      this.data = data;
    }

    public String getCacheKey() {
      return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
      this.cacheKey = cacheKey;
    }
  }
}
