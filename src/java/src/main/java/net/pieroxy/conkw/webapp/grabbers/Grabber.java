package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Grabber {
  private Logger LOGGER = createLogger();

  private File storageFolder = new File(System.getProperty("user.home"), "/.conkw/data/");
  private File tmpFolder = new File(System.getProperty("user.home"), "/.conkw/tmp/");
  private Set<String> extract = new HashSet<>();

  public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  private String name;
  private Level logLevel = Level.INFO;

  Map<String, ResponseData> cachedResponses = new HashMap<>();

  public String processAction(Map parameterMap) {
    return "";
  }
  public abstract void processHttp(HttpServletRequest req);
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

  public abstract void setConfig(Map<String, String> config);

  public void initConfig(File homeDir, Map<String, String> config) throws IOException {
    setConfig(config);
    if (name == null) setName(getDefaultName());
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

  private boolean shouldExtract(String value) {
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

  @Override
  public String toString() {
    return getClass().getName() + "/" + getName();
  }

  public abstract void writeHtmlTemplate(Writer writer) throws IOException;
}
