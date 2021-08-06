package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class TimeThrottledGrabber extends AsyncGrabber {

  protected abstract CDuration getTtl();
  protected abstract void load(ResponseData res);
  protected abstract String getCacheKey();

  private long lastRun=-1;
  private File storage;
  private Map<String, String> privateData = new HashMap<>();

  /**
   * Should be overriden by subclasses that are interested to read data from the cache when
   * the instance starts and loads its data from a cache. This implementation does nothing.
   * @param data
   * @param privateData
   */
  protected void cacheLoaded(ResponseData data, Map<String, String> privateData) {
  }

  /**
   * Allows subclasses to store data in the cache that is not exposed through the API.
   * @param privateData
   */
  protected void setPrivateData(Map<String, String> privateData) {
    this.privateData = privateData;
  }

  private File getCacheFile() {
    if (storage==null) {
      storage = new File(getStorage(), getName()+".cache.json");
    }
    return storage;
  }

  private ResponseData loadCacheFile() {
    File cacheFile = getCacheFile();
    if (!cacheFile.exists()) {
      log(Level.INFO, "Cache file not found, might be the first run.");
      return null;
    }
    try (FileInputStream fis = new FileInputStream(cacheFile)) {
      CachedData data = JsonHelper.getJson().deserialize(CachedData.class, fis);
      if (data.getCacheKey().equals(getCacheKey())) {
        privateData = data.getPrivateData();
        if (privateData == null) privateData = new HashMap<>();
        cacheLoaded(data.getData(), data.getPrivateData());
        return data.getData();
      } else {
        log(Level.INFO, "Configuration has changed, discarding cached data.");
      }
    } catch (Exception e) {
      log(Level.SEVERE, "Could not load cached data file.", e);
    }
    return null;
  }

  private void writeCacheFile(ResponseData data) {
    CachedData cdata = new CachedData();
    cdata.setCacheKey(getCacheKey());
    cdata.setData(data);
    Map<String, String> pd = privateData;
    if (pd!=null && pd.size()>0) {
      cdata.setPrivateData(new HashMap<>());
      cdata.getPrivateData().putAll(pd);
    }
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
  public final boolean changed() {
    return getTtl().isExpired(lastRun, System.currentTimeMillis());
  }

  @Override
  public final ResponseData grabSync() {
    log(Level.FINE, "grabSync() :: begin");
    if (lastRun==-1) {
      ResponseData data = loadCacheFile();
      if (data!=null && data.getErrors().isEmpty()) {
        log(Level.INFO, "Loading from cache " + getCacheFile().getAbsolutePath());
        lastRun = data.getTimestamp();
        log(Level.FINE, "grabSync() :: loaded from cache");
        return data;
      }
    }
    try {
      ResponseData r = new ResponseData(this, System.currentTimeMillis());
      load(r);
      lastRun = System.currentTimeMillis();
      return r;
    } catch (Exception e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
      return new ResponseData(this, System.currentTimeMillis());
    }
  }

  @Override
  public void saveData(ResponseData r) {
    writeCacheFile(r);
  }

  public static class CachedData {
    private ResponseData data;
    private String cacheKey;
    private Map<String, String> privateData;

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

    public Map<String, String> getPrivateData() {
      return privateData;
    }

    public void setPrivateData(Map<String, String> privateData) {
      this.privateData = privateData;
    }
  }
}
