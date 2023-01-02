package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.api.metadata.grabberConfig.ConfigField;
import net.pieroxy.conkw.api.metadata.grabberConfig.GrabberConfigMessage;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class FileGrabber extends AsyncGrabber<SimpleCollector, FileGrabber.FileGrabberConfig> {
  File file;
  Path path;
  long lastTimestamp = 0;

  @Override
  public boolean changed(SimpleCollector c) {
    try {
      return Files.getLastModifiedTime(path).toMillis() != lastTimestamp;
    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
      return false;
    }
  }

  @Override
  public SimpleCollector getDefaultCollector(boolean includeAccumulatorIfAny) {
    return new SimpleCollector(this, DEFAULT_CONFIG_KEY, includeAccumulatorIfAny ? getDefaultAccumulator() : null);
  }

  @Override
  public FileGrabberConfig getDefaultConfig() {
    return new FileGrabberConfig();
  }

  @Override
  public void grabSync(SimpleCollector c) {
    try {
      long ts = Files.getLastModifiedTime(path).toMillis();
      Properties p = new Properties();
      try (Reader reader = new FileReader(file)) {
        p.load(reader);
        for (String pname : p.stringPropertyNames()) {
          if (pname.startsWith("num_")) {
            try {
              c.collect(pname.substring(4), Double.parseDouble(p.getProperty(pname)));
            } catch (Exception e) {
              log(Level.SEVERE, "Error: unparseable property " + pname, e);
            }
          }
          if (pname.startsWith("str_")) {
            c.collect(pname.substring(4), p.getProperty(pname));
          }
        }
      }
      lastTimestamp = ts;
    } catch (Exception e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
      c.addError(getName()+":"+e.getMessage());
    }
  }

  @Override
  public void initializeGrabber(File homeDir) {
    super.initializeGrabber(homeDir);
    this.file = new File(getConfig().getFile());
    this.path = this.file.toPath();
    this.log(Level.INFO, "FileGrabber Name is " + getName() + " for file " + this.file.getAbsolutePath());
  }

  @Override
  public List<GrabberConfigMessage> validateConfiguration(FileGrabberConfig config) {
    List<GrabberConfigMessage> result = super.validateConfiguration(config);
    File f = new File(config.getFile());
    if (!f.exists()) {
      result.add(new GrabberConfigMessage(false, "file", config.getFile() + " does not exist."));
    } else if (!f.isFile()) {
      result.add(new GrabberConfigMessage(false, "file", config.getFile() + " is not a regular file."));
    }
    return result;
  }

  @Override
  public String getDefaultName() {
    return "file";
  }

  public static class FileGrabberConfig {
    @ConfigField(
            label = "File to monitor for metrics"
    )
    String file;

    public String getFile() {
      return file;
    }

    public void setFile(String file) {
      this.file = file;
    }
  }
}
