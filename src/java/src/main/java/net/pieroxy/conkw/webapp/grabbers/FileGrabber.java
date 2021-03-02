package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

public class FileGrabber extends AsyncGrabber {
  File file;
  Path path;
  long lastTimestamp = 0;

  @Override
  public void processHttp(HttpServletRequest req) {
  }

  @Override
  public boolean changed() {
    try {
      return Files.getLastModifiedTime(path).toMillis() != lastTimestamp;
    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
      return false;
    }
  }

  @Override
  public ResponseData grabSync() {
    try {
      long ts = Files.getLastModifiedTime(path).toMillis();
      ResponseData r = new ResponseData(this, ts);
      Properties p = new Properties();
      try (Reader reader = new FileReader(file)) {
        p.load(reader);
        for (String pname : p.stringPropertyNames()) {
          if (pname.startsWith("num_")) {
            try {
              r.addMetric(pname.substring(4), Double.parseDouble(p.getProperty(pname)));
            } catch (Exception e) {
              log(Level.SEVERE, "Error: unparseable property " + pname, e);
            }
          }
          if (pname.startsWith("str_")) {
            r.addMetric(pname.substring(4), p.getProperty(pname));
          }
        }
      }
      lastTimestamp = ts;
      return r;
    } catch (Exception e) {
      log(Level.SEVERE, "Grabbing " + getName(), e);
      ResponseData r = new ResponseData(this, System.currentTimeMillis());
      r.addError(getName()+":"+e.getMessage());
      return r;
    }
  }

  @Override
  public void setConfig(Map<String, String> config){
    this.log(Level.INFO, "FileGrabber Name is " + config.get("name"));
    this.file = new File((String)config.get("file"));
    this.path = this.file.toPath();
  }

  @Override
  public String getDefaultName() {
    return "file";
  }

  @Override
  public void writeHtmlTemplate(Writer writer) throws IOException {
    ResponseData rd = grabSync();
    // TODO implement it
  }
}
