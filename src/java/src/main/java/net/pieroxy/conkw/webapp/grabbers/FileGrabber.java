package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

public class FileGrabber extends AsyncGrabber<SimpleCollector> {
  File file;
  Path path;
  long lastTimestamp = 0;

  @Override
  public boolean changed() {
    try {
      return Files.getLastModifiedTime(path).toMillis() != lastTimestamp;
    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
      return false;
    }
  }

  public SimpleCollector getDefaultCollector() {
    return new SimpleTransientCollector(this);
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
  public void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs){
    this.log(Level.INFO, "FileGrabber Name is " + getName());
    this.file = new File((String)config.get("file"));
    this.path = this.file.toPath();
  }

  @Override
  public String getDefaultName() {
    return "file";
  }
}
