package net.pieroxy.conkw.grabbersBase;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public abstract class TimeThrottledGrabber extends AsyncGrabber<SimpleCollector> {
  protected abstract CDuration getDefaultTtl();
  protected abstract void load(SimpleCollector res);
  protected abstract String getCacheKey();
  protected abstract void applyConfig(Map<String, String> config, Map<String, Map<String, String>> configs);

  private CDuration ttl;
  private CDuration errorTtl;
  private boolean lastGrabHadErrors = false;
  private Map<String, SimpleCollector> collectorsStore = new HashMap<>();
  private boolean loaded, hasChanged;

  @Override
  public SimpleCollector getDefaultCollector() {
    SimpleTransientCollector res = new SimpleTransientCollector(this, DEFAULT_CONFIG_KEY);
    res.setTimestamp(0);
    res.collectionDone();
    return res;
  }

  protected final void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
    ttl = getDefaultTtl();
    errorTtl = new CDuration(Math.max(1, Math.min(60, getDefaultTtl().asSeconds() / 10)));

    ttl = getDurationProperty("ttl", config, ttl);
    errorTtl = getDurationProperty("errorTtl", config, errorTtl);

    applyConfig(config, configs);
  }

  protected CDuration getTtl() {
    return lastGrabHadErrors ? errorTtl : ttl;
  }

  private File storage;
  private Map<String, String> privateData = new HashMap<>();

  /**
   * Should be overriden by subclasses that are interested to read data from the cache when
   * the instance starts and loads its data from a cache. This implementation does nothing.
   * @param data
   * @param privateData
   */
  protected void cacheLoaded(Map<String,ResponseData> data, Map<String, String> privateData) {
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

  private synchronized void loadCacheFile() {
    File cacheFile = getCacheFile();
    if (!cacheFile.exists()) {
      log(Level.INFO, "Cache file not found, might be the first run.");
      return;
    }
    try (FileInputStream fis = new FileInputStream(cacheFile)) {
      CachedData data = JsonHelper.getJson().deserialize(CachedData.class, fis);
      privateData = data.getPrivateData();
      if (privateData == null) privateData = new HashMap<>();
      cacheLoaded(data.getDatasets(), data.getPrivateData());

      log(Level.INFO, "Loading from cache " + getCacheFile().getAbsolutePath());
      lastGrabHadErrors = false;
      for (Map.Entry<String, ResponseData> entry : data.getDatasets().entrySet()) {
        String configKey = entry.getKey();
        ResponseData rd = entry.getValue();
        lastGrabHadErrors = lastGrabHadErrors || rd.getErrors().size()>0;
        SimpleTransientCollector c = new SimpleTransientCollector(this, configKey);
        c.copyDataFrom(rd);
        collectorsStore.put(configKey, c);
        c.collectionDone();
      }
    } catch (Exception e) {
      log(Level.SEVERE, "Could not load cached data file.", e);
    }
  }

  private void writeCacheFile(Collection<SimpleCollector> data) {
    log(Level.FINE, "writeCacheFile() :: begin");
    try (CachedData cdata = new CachedData()) {
      data.forEach(c -> cdata.getDatasets().put(c.getConfigKey(), c.getDataCopy()));
      Map<String, String> pd = privateData;
      if (pd != null && pd.size() > 0) {
        cdata.setPrivateData(HashMapPool.getInstance().borrow());
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
  }

  @Override
  public final boolean changed(SimpleCollector c) {
    if (canLogFine()) log(Level.FINE, "HasChanged? " + c.getTimestamp() + " : " + getTtl().isExpired(c.getTimestamp(), System.currentTimeMillis()));
    return getTtl().isExpired(c.getTimestamp(), System.currentTimeMillis());
  }

  @Override
  public final void grabSync(SimpleCollector c) {
    log(Level.FINE, "grabSync() :: begin");
    if (!loaded) {
      loaded = true;
      loadCacheFile();
    }
    try {
      SimpleCollector collector = getOrCreateCollector(c);
      if (changed(collector)) {
        load(collector);
        log(Level.FINE, "grabSync() :: Loaded data.");
        collector.setTimestamp(System.currentTimeMillis());
        collector.collectionDone();
        hasChanged = true;
      } else {
        log(Level.FINE, "grabSync() :: Cache was fresh, no need to reload.");
      }
      c.copyDataFrom(collector);
    } catch (Exception e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
      c.addError("Error while grabbing " + getName() + ": " + e.getMessage());
    }
  }

  private SimpleCollector getOrCreateCollector(SimpleCollector c) {
    SimpleCollector collector = collectorsStore.get(c.getConfigKey());
    if (collector == null) {
      collector = new SimpleTransientCollector(this, c.getConfigKey());
      collector.setTimestamp(0);
      collector.collectionDone();
      if (canLogFine()) log(Level.FINE, "Creating collector for config " + c.getConfigKey());
      Map<String, SimpleCollector> newSCMap = new HashMap<>(collectorsStore);
      newSCMap.put(c.getConfigKey(), collector);
      collectorsStore = newSCMap;
    }
    return collector;
  }

  @Override
  public void collectionRoundIsDone(Collection<SimpleCollector> r) {
    if (hasChanged) {
      writeCacheFile(r);
      hasChanged = false;
    }
    lastGrabHadErrors = false;
    for (SimpleCollector c : r) {
      lastGrabHadErrors = lastGrabHadErrors || c.hasError();
    }
  }

  public static class CachedData implements AutoCloseable {
    private Map<String, ResponseData> datasets = HashMapPool.getInstance().borrow();
    private Map<String, String> privateData;


    public Map<String, String> getPrivateData() {
      return privateData;
    }

    public void setPrivateData(Map<String, String> privateData) {
      this.privateData = privateData;
    }

    public Map<String, ResponseData> getDatasets() {
      return datasets;
    }

    public void setDatasets(Map<String, ResponseData> datasets) {
      this.datasets = datasets;
    }

    @Override
    public void close() {
      datasets.values().forEach(rd -> rd.close());
      HashMapPool.getInstance().giveBack(datasets);
      datasets = null;
      if (privateData!=null) {
        HashMapPool.getInstance().giveBack(privateData);
        privateData = null;
      }
    }
  }
}
