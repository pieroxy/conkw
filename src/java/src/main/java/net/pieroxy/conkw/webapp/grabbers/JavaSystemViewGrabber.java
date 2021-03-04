package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.utils.OsCheck;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.management.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JavaSystemViewGrabber extends AsyncGrabber {
  private final Logger LOGGER = Logger.getLogger(this.getClass().getName());


  static final String NAME = "sys";

  private OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
  private MBeanServer pfBean = ManagementFactory.getPlatformMBeanServer();
  private List<String> mountPoints;
  private List<FileStore> mountPointsStores;
  private ObjectName on = null;
  private Long cache_totalmem;
  private Long cache_totalswp;

  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public synchronized ResponseData grabSync() {
    ResponseData r = new ResponseData(this, System.currentTimeMillis());
    extract(r, "sys", this::grabSys, Duration.ofMinutes(1));
    extract(r, "cpu", this::grabCpu, Duration.ZERO);
    extract(r, "mem", this::grabMem, Duration.ZERO);
    extract(r, "freespace", this::getFreeSpace, Duration.ZERO);
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
    long availablemem = readInternalValueAsLong("FreePhysicalMemorySize");
    res.addMetric("ramAvailable", availablemem);
    if (cache_totalmem==null) cache_totalmem = readInternalValueAsLong ("TotalPhysicalMemorySize");

    res.addMetric("ramTotal", cache_totalmem);
    res.addMetric("ramUsed", cache_totalmem - availablemem);

    long swapfree = readInternalValueAsLong("FreeSwapSpaceSize");
    if (cache_totalswp == null) cache_totalswp = readInternalValueAsLong("TotalSwapSpaceSize");

    res.addMetric("swapTotal", cache_totalswp);
    res.addMetric("swapUsed", cache_totalswp-swapfree);
    res.addMetric("swapFree", swapfree);
  }

  private void getFreeSpace(ResponseData r) {
    if (mountPoints==null || mountPoints.isEmpty()) return;
    StringBuilder mps = new StringBuilder();
    for (int i=0 ; i<mountPoints.size() ; i++)
    {
      String mp = mountPoints.get(i);
      FileStore store = mountPointsStores.get(i);
      if (store != null) {
        try {
          if (mps.length()>0) mps.append(',');
          mps.append(mp);
          r.addMetric("freespace_total_" + mp, (double) store.getTotalSpace());
          r.addMetric("freespace_usable_" + mp, (double) store.getUsableSpace());
          r.addMetric("freespace_used_" + mp, (double) (store.getTotalSpace() - store.getUsableSpace()));
        } catch (IOException e) {
          log(Level.SEVERE, "Grabbing free space for " + mp, e);
        }
      }
    }
    r.addMetric("freespace_mountpoints", mps.toString());
  }


  long readInternalValueAsLong(String attr) {
    Object o = null;
    try {
      if (on == null) on = new ObjectName("java.lang", "type", "OperatingSystem");
      o = pfBean.getAttribute(on, attr);
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
      if (on == null) on = new ObjectName("java.lang", "type", "OperatingSystem");
      o = pfBean.getAttribute(on, attr);
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
    String mpstr = config.get("mountPoints");
    if (mpstr == null) {
      if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Windows) {
        File[]roots = File.listRoots();
        mountPoints = Arrays.stream(roots).map(f -> f.getAbsolutePath()).collect(Collectors.toList());
      } else {
        try {
          mountPoints = StreamSupport.stream(FileSystems.getDefault().getFileStores().spliterator(), false)
              .map(f -> f.toString().split("\\(")[0].trim())
              .filter(s -> !s.startsWith("/dev"))
              .filter(s -> !s.startsWith("/snap"))
              .filter(s -> !s.startsWith("/sys"))
              .filter(s -> !s.startsWith("/System"))
              .filter(s -> !s.startsWith("/proc"))
              .filter(s -> !s.startsWith("/run"))
              .collect(Collectors.toList());
        } catch (Exception e) {
          log(Level.WARNING, "Could not detect file stores", e);
          mountPoints.clear();
          if (new File("/").exists()) mountPoints.add("/");
          if (new File("C:\\").exists()) mountPoints.add("C:\\");
        }
      }
      if (canLogInfo()) log(Level.INFO, "Detected mount points to be " + mountPoints.stream().collect(Collectors.joining(",")));
    } else {
      mountPoints = Arrays.asList(mpstr.split(","));
    }
    mountPointsStores = new ArrayList<>(mountPoints.size());
    for (String mp : mountPoints) {
      try {
        mountPointsStores.add(Files.getFileStore(Paths.get(mp)));
      } catch (IOException e) {
        if (canLogFine()) {
          log(Level.SEVERE, "Getting FileStore for " + mp, e);
        } else {
          log(Level.WARNING, "Getting FileStore for " + mp + " failed.");
        }
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
