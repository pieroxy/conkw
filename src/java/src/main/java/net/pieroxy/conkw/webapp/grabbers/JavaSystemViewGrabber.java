package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.utils.OsCheck;
import net.pieroxy.conkw.utils.duration.CDuration;

import javax.management.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.UnknownHostException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class JavaSystemViewGrabber extends AsyncGrabber<SimpleCollector, JavaSystemViewGrabber.JavaSystemViewGrabberConfig> {
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
  public boolean changed(SimpleCollector c) {
    return true;
  }

  public SimpleCollector getDefaultCollector() {
    return new SimpleTransientCollector(this, DEFAULT_CONFIG_KEY);
  }

  @Override
  public void grabSync(SimpleCollector c) {
    extract(c, "sys", this::grabSys, CDuration.ONE_MINUTE);
    extract(c, "cpu", this::grabCpu, CDuration.ZERO);
    extract(c, "mem", this::grabMem, CDuration.ZERO);
    extract(c, "freespace", this::getFreeSpace, CDuration.ZERO);
  }

  private void grabSys(SimpleCollector c) {
    c.collect("arch", osBean.getArch());
    c.collect("nbcpu", osBean.getAvailableProcessors());
    c.collect("osname", osBean.getName());
    c.collect("osversion", osBean.getVersion());
    c.collect("user", System.getProperty("user.name"));
    try {
      c.collect("hostname", java.net.InetAddress.getLocalHost().getHostName());
    } catch (UnknownHostException e) {
      LOGGER.log(Level.SEVERE, "Getting hostname", e);
    }
  }
  private void grabCpu(SimpleCollector c) {
    c.collect("systemloadavg", osBean.getSystemLoadAverage());
    c.collect("processCpuUsage", readInternalValueAsDouble("ProcessCpuLoad"));
    c.collect("ProcessCpuTime", readInternalValueAsLong("ProcessCpuTime"));
    c.collect("totalCpuUsage", readInternalValueAsDouble("SystemCpuLoad"));
  }
  private void grabMem(SimpleCollector c) {
    long availablemem = readInternalValueAsLong("FreePhysicalMemorySize");
    c.collect("ramAvailable", availablemem);
    if (cache_totalmem==null) cache_totalmem = readInternalValueAsLong ("TotalPhysicalMemorySize");

    c.collect("ramTotal", cache_totalmem);
    c.collect("ramUsed", cache_totalmem - availablemem);

    long swapfree = readInternalValueAsLong("FreeSwapSpaceSize");
    if (cache_totalswp == null) cache_totalswp = readInternalValueAsLong("TotalSwapSpaceSize");

    c.collect("swapTotal", cache_totalswp);
    c.collect("swapUsed", cache_totalswp-swapfree);
    c.collect("swapFree", swapfree);
  }

  private void getFreeSpace(SimpleCollector c) {
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
          c.collect("freespace_total_" + mp, (double) store.getTotalSpace());
          c.collect("freespace_usable_" + mp, (double) store.getUsableSpace());
          c.collect("freespace_used_" + mp, (double) (store.getTotalSpace() - store.getUsableSpace()));
        } catch (IOException e) {
          log(Level.SEVERE, "Grabbing free space for " + mp, e);
        }
      }
    }
    c.collect("freespace_mountpoints", mps.toString());
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
  public void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
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

  public static class JavaSystemViewGrabberConfig {

  }
}
