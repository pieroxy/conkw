package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public abstract class Grabber {
  public static final int LOG_ERROR = 0;
  public static final int LOG_WARNING = 1;
  public static final int LOG_INFO = 2;
  public static final int LOG_DEBUG = 3;
  public static final int LOG_FINE = 4;

  private File storageFolder = new File(System.getProperty("user.home"), "/.conkw/data/");
  private File tmpFolder = new File(System.getProperty("user.home"), "/.conkw/tmp/");

  public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  public static String NAME_CONFIG_PROPERTY = "name";
  public static String LOGLEVEL_CONFIG_PROPERTY = "logLevel";
  private String name;
  private int logLevel = LOG_INFO;

  public String processAction(Map parameterMap) {
    return "";
  }
  public abstract void processHttp(HttpServletRequest req);
  public abstract ResponseData grab();
  public abstract void dispose();

  public abstract void setConfig(Map<String, String> config);

  public void initConfig(File homeDir, Map<String, String> config) throws IOException {
    tmpFolder = new File(homeDir, "tmp");
    storageFolder = new File(homeDir, "data");
    Files.createDirectories(getStorage().toPath());
    Files.createDirectories(getTmp().toPath());

    logLevel = Integer.parseInt(String.valueOf(config.getOrDefault(LOGLEVEL_CONFIG_PROPERTY, "2")));
    setConfig(config);
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

  protected void setNameFromConfig(Map<String, String> config, String defaultValue) {
    setName(String.valueOf(config.getOrDefault(NAME_CONFIG_PROPERTY, defaultValue)));
  }

  protected final void log(int loglevel, String message) {
    if (this.logLevel>=loglevel) {
      String date, ll;
      synchronized (sdf) {
        date = sdf.format(new Date());
      }
      switch (loglevel) {
        case LOG_ERROR:
          ll = "ERROR";
          break;
        case LOG_WARNING:
          ll = "WARN ";
          break;
        case LOG_INFO:
          ll = "INFO ";
          break;
        case LOG_DEBUG:
          ll = "DEBUG";
          break;
        case LOG_FINE:
          ll = "FINE ";
          break;
        default:
          ll = "WTH? ";
          break;
      }
      System.out.println(date + " " + ll + " " + message);
    }
  }

  protected final boolean isInfo() {
    return logLevel >= LOG_INFO;
  }
  protected final boolean isDebug() {
    return logLevel >= LOG_DEBUG;
  }
  protected final boolean isFine() {
    return logLevel >= LOG_FINE;
  }

  public final String getName() {
    return name;
  }

  private final void setName(String name) {
    this.name = name;
  }

  public abstract void writeHtmlTemplate(Writer writer) throws IOException;
}
