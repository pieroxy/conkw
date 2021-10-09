package net.pieroxy.conkw.grabbersBase;

import net.pieroxy.conkw.collectors.Collector;
import net.pieroxy.conkw.collectors.EmptyCollector;
import net.pieroxy.conkw.utils.LongHolder;
import net.pieroxy.conkw.utils.TimedData;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class Grabber<T extends Collector> {
  private Logger LOGGER = createLogger();
  private static final long CONF_EXPIRATION_MS = 10000; // 10s
  private final Collector EMPTY_COLLECTOR = new EmptyCollector(this);
  public static final String DEFAULT_CONFIG_KEY="default";

  private File storageFolder=null;
  private File tmpFolder=null;
  private Set<String> extract = new HashSet<>();

  private String name;

  private Level logLevel;

  Map<String, LongHolder> maxValues = new HashMap<>();
  Map<String, TimedData<T>> extractedByConfiguration = HashMapPool.getInstance().borrow();
  private long lastConfigPurge;

  public String processAction(Map<String, String[]> parameterMap) {
    return "";
  }
  public abstract void collect(T c);
  public abstract void dispose();
  public abstract String getDefaultName();
  public abstract T getDefaultCollector();

  private void gcConfigurations() {
    Map<String,TimedData<T>> nm = new HashMap<>(extractedByConfiguration);
    boolean deleted = false;
    for (String s : nm.keySet()) {
      TimedData td = nm.get(s);
      if (td.getAge() > CONF_EXPIRATION_MS) {
        nm.remove(s);
        deleted = true;
      }
    }

    if (deleted) extractedByConfiguration = nm;
    lastConfigPurge = System.currentTimeMillis();
  }

  public final void collect() {
    getActiveCollectors().forEach(this::collect);
  }

  public Collection<T> getActiveCollectors() {
    long now = System.currentTimeMillis();
    if (now-lastConfigPurge > CONF_EXPIRATION_MS) gcConfigurations();
    return extractedByConfiguration.values().stream().map(td -> td.getData()).collect(Collectors.toList());
  }

  public Collector getCollectorToUse(String config) {
    TimedData<? extends Collector> res = extractedByConfiguration.get(config);
    if (res==null) return EMPTY_COLLECTOR; // First call might encounter this situation.
    res.useNow();
    return res.getData();
  }

  public T parseCollector(String param) {
    throw new RuntimeException("Grabber " + this.getClass().getSimpleName() + " does not take any parameters");
  }

  public void addActiveCollector(String param) {
    if (extractedByConfiguration.containsKey(param)) return;
    Map<String,TimedData<T>> nm = HashMapPool.getInstance().borrow(extractedByConfiguration);
    if (param == null) {
      nm.put(null, new TimedData(getDefaultCollector()));
    } else {
      nm.put(param, new TimedData(parseCollector(param)));
    }
    Map tmp = extractedByConfiguration;
    extractedByConfiguration = nm;
    HashMapPool.getInstance().giveBack(tmp);
  }

  protected abstract void setConfig(Map<String, String> config, Map<String, Map<String, String>> namedConfigs);

  public void initConfig(File homeDir, Map<String, String> config, Map<String, Map<String, String>> namedConfigs) {
    storageFolder = new File(homeDir, "data");
    tmpFolder = new File(homeDir, "tmp");
    if (name == null) setName(getDefaultName());
    setConfig(config, namedConfigs);
  }

  public void setLogLevel(Level l) {
    if (l != logLevel && l!=Level.INFO) LOGGER.info("LogLevel is " + l);
    logLevel = l;
    LOGGER.setLevel(l);
  }

  public void setExtractProperties(String[]toExtract) {
    for (String val : toExtract) {
      extract.add(val);
    }
  }

  protected boolean shouldExtract(String value) {
    return extract.isEmpty() || extract.contains(value);
  }

  public File getStorage() {
    return storageFolder;
  }
  public File getTmp() {
    return tmpFolder;
  }
  protected File getTmp(String filename) {
    return new File(getTmp(), filename);
  }


  protected final void log(Level loglevel, String message) {
    LOGGER.log(loglevel, message);
  }
  protected final void log(Level loglevel, String message, Throwable t) {
    LOGGER.log(loglevel, message, t);
  }
  protected final boolean canLogInfo() {
    return LOGGER.isLoggable(Level.INFO);
  }
  protected final boolean canLogFine() {
    return LOGGER.isLoggable(Level.FINE);
  }
  protected final boolean canLogFiner() {
    return LOGGER.isLoggable(Level.FINER);
  }

  public final String getName() {
    return name;
  }

  protected int getIntProperty(String propertyName, Map<String, String> config, int defaultValue) {
    String propValue = config.get(propertyName);
    if (propValue != null) {
      return Integer.parseInt(propValue);
    }
    return defaultValue;
  }

  protected String getStringProperty(String propertyName, Map<String, String> config, String defaultValue) {
    String propValue = config.get(propertyName);
    if (propValue != null) {
      return propValue;
    }
    return defaultValue;
  }

  protected CDuration getDurationProperty(String propertyName, Map<String, String> config, CDuration defaultValue) {
    String propValue = config.get(propertyName);
    if (propValue != null) {
      return CDurationParser.parse(propValue);
    }

    return defaultValue;
  }

  protected Pattern getRegexpProperty(String propertyName, Map<String, String> config, int flags) {
    String propValue = config.get(propertyName);
    if (propValue != null) {
      return Pattern.compile(propValue, flags);
    }
    return null;
  }

  public void setName(String name) {
    this.name = name;
    LOGGER = createLogger();
  }

  private Logger createLogger() {
    String n = getName();
    if (n==null) n = getDefaultName();
    Logger l = Logger.getLogger(this.getClass().getName() + "/" + n);
    if (logLevel!=null) l.setLevel(logLevel);
    return l;
  }

  @Override
  public String toString() {
    return getClass().getName() + "/" + getName();
  }
}