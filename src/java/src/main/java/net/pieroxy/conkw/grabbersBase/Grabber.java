package net.pieroxy.conkw.grabbersBase;

import net.pieroxy.conkw.collectors.Collector;
import net.pieroxy.conkw.collectors.EmptyCollector;
import net.pieroxy.conkw.utils.TimedData;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class Grabber<T extends Collector, C> {
  private Logger LOGGER = createLogger();
  private static final long CONF_EXPIRATION_MS = 10000; // 10s
  private final Collector EMPTY_COLLECTOR = new EmptyCollector(this);
  public static final String DEFAULT_CONFIG_KEY="default";

  private File storageFolder=null;
  private File tmpFolder=null;
  private Set<String> extract = new HashSet<>();
  private C config;

  private String name;

  private Level logLevel;

  Map<String, TimedData<T>> extractedByConfiguration = new HashMap<>();
  private long lastConfigPurge;

  public String processAction(Map<String, String[]> parameterMap) {
    return "";
  }
  public abstract void collect(T c);
  public abstract void dispose();
  public abstract String getDefaultName();
  public abstract T getDefaultCollector();

  /**
   * Override if you want to provide configuration options.
   * @return The default configuration, to be overridden by the config file. Note that the class provided must
   * not include any member of types with no default constructors.
   */
  public C getDefaultConfig() {
    return null;
  }

  public C getConfig() {
    return config;
  }

  public void setConfig(C config) {
    this.config = config;
  }

  private void gcConfigurations() {
    Map<String, TimedData<T>> nm = new HashMap<>(extractedByConfiguration);
    Set<TimedData<T>> toClose = new HashSet<>();
    for (String s : nm.keySet()) {
      TimedData td = nm.get(s);
      if (canLogFiner())
        log(Level.FINER, "GC :: " + td + " ??? " + (td.getAge() > CONF_EXPIRATION_MS) + " $$$ " + super.toString());
      if (td.getAge() > CONF_EXPIRATION_MS) {
        toClose.add(nm.remove(s));
      }
    }

    if (!toClose.isEmpty()) {
      extractedByConfiguration = nm;
      for (TimedData<T> td : toClose) {
        if (canLogFine()) log(Level.FINE, "GCed collector " + td);
        td.close();
      }
    }
    lastConfigPurge = System.currentTimeMillis();
  }

  public final void collect() {
    if (canLogFine()) log(Level.FINE,  "Collecting " + getActiveCollectors().stream().map(c -> c.getConfigKey()).collect(Collectors.joining(",")));
    getActiveCollectors().forEach(this::collect);
  }

  public Collection<T> getActiveCollectors() {
    long now = System.currentTimeMillis();
    if (now-lastConfigPurge > CONF_EXPIRATION_MS) gcConfigurations();
    return extractedByConfiguration.values().stream().map(td -> td.getData()).collect(Collectors.toList());
  }

  public Collector getCollectorToUse(String config) {
    if (config == null) config = DEFAULT_CONFIG_KEY;
    TimedData<? extends Collector> res = extractedByConfiguration.get(config);
    if (res==null) return EMPTY_COLLECTOR; // First call might encounter this situation.
    if (canLogFiner()) log(Level.FINER, "About to use " + res + " out of " + extractedByConfiguration.size() + " $$$ " + super.toString());
    res.useNow();
    return res.getData();
  }

  public T parseCollector(String param) {
    throw new RuntimeException("Grabber " + this.getClass().getSimpleName() + " does not take any parameters");
  }

  public void addActiveCollector(String param) {
    if (param == null) param = DEFAULT_CONFIG_KEY;
    if (extractedByConfiguration.containsKey(param)) {
      return;
    }
    Map<String, TimedData<T>> nm = new HashMap<>(extractedByConfiguration);
    TimedData<T> created;
    if (param == null || param.equals(DEFAULT_CONFIG_KEY)) {
      nm.put(DEFAULT_CONFIG_KEY, created = new TimedData(getDefaultCollector()));
    } else {
      nm.put(param, created = new TimedData(parseCollector(param)));
    }
    if (canLogFiner())
      log(Level.FINER, "Created " + extractedByConfiguration.get(param) + " from param '" + param + "'");

    extractedByConfiguration = nm;
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