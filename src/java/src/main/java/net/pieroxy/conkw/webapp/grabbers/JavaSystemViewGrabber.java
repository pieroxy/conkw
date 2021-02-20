package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.Charset;
import java.util.Map;

public class JavaSystemViewGrabber extends AsyncGrabber {
  static final String NAME = "sys";

  private OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public ResponseData grabSync() {
    ResponseData res = new ResponseData(getName(), System.currentTimeMillis());
    res.addMetric("arch", osBean.getArch());
    res.addMetric("nbcpu", osBean.getAvailableProcessors());
    res.addMetric("systemloadavg", osBean.getSystemLoadAverage());
    return res;
  }

  @Override
  public void processHttp(HttpServletRequest req) {
  }

  @Override
  public void setConfig(Map<String, String> config) {
    setNameFromConfig(config, NAME);
  }

  @Override
  public void writeHtmlTemplate(Writer writer) throws IOException {
    String namespaceAttr = "cw-ns=\""+getName()+"\"";
    InputStream is = getClass().getClassLoader().getResourceAsStream("javasystemviewgrabber.template.html");
    BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF8")));
    String line;
    while ((line = br.readLine())!=null) {
      writer.write(line.replaceAll("cw-ns=\"sys\"", namespaceAttr));
    }
  }
}
