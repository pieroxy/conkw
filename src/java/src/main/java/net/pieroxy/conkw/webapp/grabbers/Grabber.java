package net.pieroxy.conkw.webapp.grabbers;

import com.dslplatform.json.processor.LogLevel;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Grabber {
  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  private File storageFolder = new File(System.getProperty("user.home"), "/.conkw/data/");
  private File tmpFolder = new File(System.getProperty("user.home"), "/.conkw/tmp/");
  private Set<String> extract = new HashSet<>();

  public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  public static String NAME_CONFIG_PROPERTY = "name";
  public static String EXTRACT_CONFIG_PROPERTY = "extract";
  public static String LOGLEVEL_CONFIG_PROPERTY = "logLevel";
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

  public void extractFixedDelay(ResponseData toFill, String extractName, ExtractMethod method, java.time.Duration delay) {
    if (shouldExtract(extractName)) {
      long now = System.currentTimeMillis();
      ResponseData cached = cachedResponses.get(extractName);
      if (cached==null || (now-cached.getTimestamp() > delay.toMillis())) {
        if (canLogFiner()) {
          if (cached == null)
            log(Level.INFO,now +  " Grabbing " + extractName + " cached on null with delay " + delay.toMillis());
          else
            log(Level.INFO,now + " Grabbing " + extractName + " cached on " + cached.getTimestamp() + " with delay " + delay.toMillis());
        }
        cached = new ResponseData(extractName, now);
        method.extract(cached);
        cachedResponses.put(extractName, cached);
      }
      toFill.getNum().putAll(cached.getNum());
      toFill.getStr().putAll(cached.getStr());
    }
  }

  public abstract void setConfig(Map<String, String> config);

  public void initConfig(File homeDir, Map<String, String> config) throws IOException {
    setNameFromConfig(config, getDefaultName());
    String llas = config.getOrDefault(LOGLEVEL_CONFIG_PROPERTY, "INFO");
    logLevel = Level.parse(llas);
    if (logLevel == null) {
      logLevel = Level.INFO;
      LOGGER.severe("Could not parse log level " + llas + ". Using INFO.");
    }
    if (logLevel != Level.INFO) LOGGER.info(getName() + " logLevel is " + logLevel);
    LOGGER.setLevel(logLevel);
    setExtractProperty(config);
    setConfig(config);
  }

  private void setExtractProperty(Map<String, String> config) {
    String v = config.get(EXTRACT_CONFIG_PROPERTY);
    if (v!=null && !v.equals("all") && !v.equals("")) {
      String[]values = v.split(",");
      for (String val : values) {
        extract.add(val);
      }
    }
  }

  protected boolean shouldExtract(String value) {
    return extract.isEmpty() || extract.contains(value);
  }

  private void setNameFromConfig(Map<String, String> config, String defaultValue) {
    setName(config.getOrDefault(NAME_CONFIG_PROPERTY, defaultValue));
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

  private final void setName(String name) {
    this.name = name;
  }

  public abstract void writeHtmlTemplate(Writer writer) throws IOException;
}
