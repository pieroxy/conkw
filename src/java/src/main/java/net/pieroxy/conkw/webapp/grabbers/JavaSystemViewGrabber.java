package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.management.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Level;

public class JavaSystemViewGrabber extends AsyncGrabber {


  static final String NAME = "sys";

  private OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
  private MBeanServer pfBean = ManagementFactory.getPlatformMBeanServer();

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
    res.addMetric("osname", osBean.getName());
    res.addMetric("osversion", osBean.getVersion());

    res.addMetric("processCpuUsage", readInternalValueAsDouble("ProcessCpuLoad"));
    res.addMetric("ProcessCpuTime", readInternalValueAsLong("ProcessCpuTime"));
    res.addMetric("totalCpuUsage", readInternalValueAsDouble("SystemCpuLoad"));

    long availablemem = readInternalValueAsLong("CommittedVirtualMemorySize");
    long freemem = readInternalValueAsLong("FreePhysicalMemorySize");
    long totalmem = readInternalValueAsLong ("TotalPhysicalMemorySize");

    res.addMetric("ramTotal", totalmem);
    res.addMetric("ramFree", freemem);
    res.addMetric("ramAvailable", availablemem);
    res.addMetric("ramUsed", totalmem - availablemem);

    long swapfree = readInternalValueAsLong("FreeSwapSpaceSize");
    long swaptotal = readInternalValueAsLong("TotalSwapSpaceSize");

    res.addMetric("swapTotal", swaptotal);
    res.addMetric("swapUsed", swaptotal-swapfree);
    res.addMetric("swapFree", swapfree);

    return res;
  }

  long readInternalValueAsLong(String attr) {
    Object o = null;
    try {
      o = pfBean.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"), attr);
      return (Long)o;
    } catch (Exception e) {
      log(Level.SEVERE, "Getting system information attribute " + attr, e);
      if (o!=null) log(Level.SEVERE, "Object was " + o.getClass().getName());
    }
    return -1;
  }

  double readInternalValueAsDouble(String attr) {
    Object o = null;
    try {
      o = pfBean.getAttribute(new ObjectName("java.lang", "type", "OperatingSystem"), attr);
      return (Double)o;
    } catch (Exception e) {
      log(Level.SEVERE, "Getting system information attribute " + attr, e);
      if (o!=null) log(Level.SEVERE, "Object was " + o.getClass().getName());
    }
    return -1;
  }

  @Override
  public void processHttp(HttpServletRequest req) {
  }

  @Override
  public void setConfig(Map<String, String> config) {
  }

  @Override
  public String getDefaultName() {
    return NAME;
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
