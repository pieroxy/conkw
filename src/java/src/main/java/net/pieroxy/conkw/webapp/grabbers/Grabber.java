package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Grabber {
  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

  private File storageFolder = new File(System.getProperty("user.home"), "/.conkw/data/");
  private File tmpFolder = new File(System.getProperty("user.home"), "/.conkw/tmp/");

  public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  public static String NAME_CONFIG_PROPERTY = "name";
  public static String LOGLEVEL_CONFIG_PROPERTY = "logLevel";
  private String name;
  private Level logLevel = Level.INFO;

  public String processAction(Map parameterMap) {
    return "";
  }
  public abstract void processHttp(HttpServletRequest req);
  public abstract ResponseData grab();
  public abstract void dispose();
  public abstract String getDefaultName();

  public abstract void setConfig(Map<String, String> config);

  public void initConfig(File homeDir, Map<String, String> config) throws IOException {
    tmpFolder = new File(homeDir, "tmp");
    storageFolder = new File(homeDir, "data");
    Files.createDirectories(getStorage().toPath());
    Files.createDirectories(getTmp().toPath());

    String llas = config.getOrDefault(LOGLEVEL_CONFIG_PROPERTY, "INFO");
    logLevel = Level.parse(llas);
    if (logLevel == null) {
      logLevel = Level.INFO;
      LOGGER.severe("Could not parse log level " + llas + ". Using INFO.");
    }
    setNameFromConfig(config, getDefaultName());
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

  private void setNameFromConfig(Map<String, String> config, String defaultValue) {
    setName(String.valueOf(config.getOrDefault(NAME_CONFIG_PROPERTY, defaultValue)));
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
