package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.LongHolder;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public abstract class Grabber {
  private Logger LOGGER = createLogger();

  private File storageFolder=null;
  private File tmpFolder=null;
  private Set<String> extract = new HashSet<>();

  public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  private String name;
  private Level logLevel = Level.INFO;

  private static volatile MaxComputer maxComputer = null;

  Map<String, ResponseData> cachedResponses = new HashMap<>();
  Map<String, LongHolder> maxValues = new HashMap<>();

  public String processAction(Map<String, String[]> parameterMap) {
    return "";
  }
  public abstract ResponseData grab();
  public abstract void dispose();
  public abstract String getDefaultName();

  public void extract(ResponseData toFill, String extractName, ExtractMethod method, java.time.Duration delay) {
    if (shouldExtract(extractName)) {
      if (delay.equals(Duration.ZERO)) {
        method.extract(toFill);
        return;
      }
      long now = System.currentTimeMillis();
      ResponseData cached = cachedResponses.get(extractName);
      if (cached==null || (now-cached.getTimestamp() > delay.toMillis())) {
        if (canLogFiner()) {
          if (cached == null)
            log(Level.FINER,now +  " Grabbing " + extractName + " cached on null with delay " + delay.toMillis());
          else
            log(Level.FINER,now + " Grabbing " + extractName + " cached on " + cached.getTimestamp() + " with delay " + delay.toMillis());
        }
        cached = new ResponseData(null, now);
        method.extract(cached);
        cachedResponses.put(extractName, cached);
      }
      toFill.getNum().putAll(cached.getNum());
      toFill.getStr().putAll(cached.getStr());
      toFill.addExtracted(extractName);
    }
  }

  protected abstract void setConfig(Map<String, String> config, Map<String, Map<String, String>> namedConfigs);

  public void initConfig(File homeDir, Map<String, String> config, Map<String, Map<String, String>> namedConfigs) {
    storageFolder = new File(homeDir, "data");
    tmpFolder = new File(homeDir, "tmp");
    if (maxComputer == null) maxComputer = new MaxComputer(getStorage());
    if (name == null) setName(getDefaultName());
    setConfig(config, namedConfigs);
  }

  public void setLogLevel(Level l) {
    if (l != logLevel) LOGGER.info(getName() + " logLevel is " + l);
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
    l.setLevel(logLevel);
    return l;
  }

  protected void computeAutoMaxPerSecond(ResponseData res, String metricName, double value) {
    res.addMetric(metricName, value);
    LongHolder lh = maxValues.get(metricName);
    if (lh == null) {
      maxValues.put(metricName, new LongHolder(System.currentTimeMillis()));
    } else {
      double ratio = (System.currentTimeMillis() - lh.value)/1000.;
      if (ratio>0.50) { // Below 0.5s things might get out of whack
        double mv = maxComputer.getMax(this, metricName, value/ratio);
        res.addMetric("max$" + metricName, mv);
      } else {
        log(Level.INFO, "Ignoring value of " +value + " for metric " + metricName + " because ratio is "  + ratio);
      }
      lh.value = System.currentTimeMillis();
    }
  }

  protected void computeAutoMaxMinAbsolute(ResponseData res, String metricName, double value) {
    res.addMetric(metricName, value);
    {
      LongHolder lh = maxValues.get(metricName);
      if (lh == null) {
        maxValues.put(metricName, new LongHolder(System.currentTimeMillis()));
      } else {
        double mv = maxComputer.getMax(this, metricName, value);
        res.addMetric("max$" + metricName, mv);
        lh.value = System.currentTimeMillis();
      }
    }
    {
      metricName = "min$" + metricName;
      value = -value;
      LongHolder lh = maxValues.get(metricName);
      if (lh == null) {
        maxValues.put(metricName, new LongHolder(System.currentTimeMillis()));
      } else {
        double mv = maxComputer.getMax(this, metricName, value);
        res.addMetric(metricName, -mv);
        lh.value = System.currentTimeMillis();
      }
    }
  }

  @Override
  public String toString() {
    return getClass().getName() + "/" + getName();
  }
}