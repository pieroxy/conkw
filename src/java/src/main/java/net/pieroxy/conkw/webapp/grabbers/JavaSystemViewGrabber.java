package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.PerformanceTools;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.management.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaSystemViewGrabber extends AsyncGrabber {
  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());


  static final String NAME = "sys";

  private OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
  private MBeanServer pfBean = ManagementFactory.getPlatformMBeanServer();
  private List<String> mountPoints;
  private List<FileStore> mountPointsStores;

  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public synchronized ResponseData grabSync() {
    ResponseData r = new ResponseData(getName(), System.currentTimeMillis());
    if (shouldExtract("sys")) grabSys(r);
    if (shouldExtract("cpu")) grabCpu(r);
    if (shouldExtract("mem")) grabMem(r);
    if (shouldExtract("freespace")) getFreeSpace(r);
    return r;
  }

  private void grabSys(ResponseData res) {
    res.addMetric("arch", osBean.getArch());
    res.addMetric("nbcpu", osBean.getAvailableProcessors());
    res.addMetric("osname", osBean.getName());
    res.addMetric("osversion", osBean.getVersion());
    res.addMetric("user", System.getProperty("user.name"));
    try {
      res.addMetric("hostname", java.net.InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      LOGGER.log(Level.SEVERE, "Getting hostname", e);
    }
  }
  private void grabCpu(ResponseData res) {
    res.addMetric("systemloadavg", osBean.getSystemLoadAverage());
    res.addMetric("processCpuUsage", readInternalValueAsDouble("ProcessCpuLoad"));
    res.addMetric("ProcessCpuTime", readInternalValueAsLong("ProcessCpuTime"));
    res.addMetric("totalCpuUsage", readInternalValueAsDouble("SystemCpuLoad"));
  }
  private void grabMem(ResponseData res) {
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
  }

  private void getFreeSpace(ResponseData r) {
    if (mountPoints.isEmpty()) return;
    for (int i=0 ; i<mountPoints.size() ; i++)
    {
      String mp = mountPoints.get(i);
      FileStore store = mountPointsStores.get(i);
      if (store != null) {
        try {
          r.addMetric("fsf_total_" + mp, (double) store.getTotalSpace());
          r.addMetric("fsf_usable_" + mp, (double) store.getUsableSpace());
          r.addMetric("fsf_used_" + mp, (double) (store.getTotalSpace() - store.getUnallocatedSpace()));
        } catch (IOException e) {
          log(Level.SEVERE, "Grabbing free space for " + mp, e);
        }
      }
    }
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
    mountPoints = Arrays.asList(config.get("mountPoints").split(","));
    if (mountPoints == null) {
      mountPoints = new ArrayList<>();
      mountPoints.add("/");
    }
    mountPointsStores = new ArrayList<>(mountPoints.size());
    for (String mp : mountPoints) {
      try {
        mountPointsStores.add(Files.getFileStore(Paths.get(mp)));
      } catch (IOException e) {
        log(Level.SEVERE, "Getting FileStore for " + mp, e);
        mountPointsStores.add(null);
      }
    }
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
      if (line.startsWith("_EMP_")) {
        line = line.substring(5);
        for (String mp : mountPoints) {
          writer.write(line.replaceAll("cw-ns=\"sys\"", namespaceAttr).replaceAll("\\$\\$", sumMountPoint(mp)).replaceAll("\\$", mp));
        }
      } else {
        writer.write(line.replaceAll("cw-ns=\"sys\"", namespaceAttr));
      }
    }
  }

  private String sumMountPoint(String mp) {
    if (mp.equals("/")) return "root";
    while (mp.length()<4) mp = " " + mp;
    if (mp.length()>4) mp = mp.substring(mp.length()-4);
    return mp;
  }
}
