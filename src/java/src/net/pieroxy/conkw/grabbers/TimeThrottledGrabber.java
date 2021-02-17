package net.pieroxy.conkw.grabbers;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.JsonHelper;
import net.pieroxy.conkw.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.Duration;

public abstract class TimeThrottledGrabber extends AsyncGrabber {

  protected abstract Duration getTtl();
  protected abstract void load(ResponseData res);

  private long lastRun=-1;
  private File storage;

  File getCacheFile() {
    if (storage==null) {
      storage = new File(getStorage(), getName()+".cache.json");
    }
    return storage;
  }

  ResponseData loadCacheFile() {
    try (FileInputStream fis = new FileInputStream(getCacheFile())) {
      ResponseData data = JsonHelper.getJson().deserialize(ResponseData.class, fis);
      return data;
    } catch (Exception e) {
      log(LOG_ERROR, e.getMessage());
      return null;
    }
  }

  void writeCacheFile(ResponseData data) {
    JsonWriter w = JsonHelper.getWriter();
    DslJson<Object> json = JsonHelper.getJson();
    synchronized (w) {
      try (OutputStream os = new FileOutputStream(getCacheFile())) {
        w.reset(os);
        json.serialize(w, data);
        w.flush();
      } catch (Exception e) {
        log(LOG_ERROR, e.getMessage());
      }
    }
  }

  @Override
  public void processHttp(HttpServletRequest req) {
  }

  @Override
  public boolean changed() {
    return System.currentTimeMillis() - lastRun > getTtl().toMillis();
  }

  @Override
  public ResponseData grabSync() {
    if (lastRun==-1) {
      ResponseData data = loadCacheFile();
      if (data!=null) {
        log(LOG_INFO, "Loading from cache " + getCacheFile().getAbsolutePath());
        lastRun = data.getTimestamp();
        return data;
      }
    }
    try {
      ResponseData r = new ResponseData(getName(), System.currentTimeMillis());
      load(r);
      lastRun = System.currentTimeMillis();
      writeCacheFile(r);
      return r;
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseData(getName(), System.currentTimeMillis());
    }
  }
}
