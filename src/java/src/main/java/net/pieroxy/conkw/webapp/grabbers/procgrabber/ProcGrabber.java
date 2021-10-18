package net.pieroxy.conkw.webapp.grabbers.procgrabber;

import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.utils.ExternalBinaryRunner;
import net.pieroxy.conkw.utils.PerformanceTools;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcGrabber extends AsyncGrabber<SimpleCollector> {
  static final String MDSTAT_FILE = "/proc/mdstat";
  static final String NAME = "proc";

  private boolean disabled = false;

  private Map<Long,Long> lastProcessesCpuUsage = HashMapPool.getInstance().borrow(100);
  // This is not the number of CPU but the number of ints to parse from the first line of /proc/stat
  private double[] lastCpuUsage = new double[7];
  private Map<String, Long> lastBlockDeviceRead = new HashMap<>();
  private Map<String, Long> lastBlockDeviceWrite = new HashMap<>();
  private Map<String, Integer> blockDeviceSectorSize = new HashMap<>();
  private Map<Long, ProcStatHelper> procStatFile = new HashMap<>();
  private int procStatFileLimit=0;
  private long lastNin, lastNout;

  private final long[]long2buffer = new long[2];
  private final double[]double3buffer = new double[3];
  private final int[]int2buffer = new int[2];
  private final byte[]babuffer1k = new byte[1024];

  private Collection<String> blockDevices;

  private File BAT_FULL, BAT_NOW, BAT_AC;
  private Boolean BAT_present;
  private long BAT_plugged;
  private double BAT_prc;
  private int BAT_counter;

  private File mdstatFile;
  private ExternalBinaryRunner blockDeviceDetector;
  private boolean autoDetectBlockDevices;

  private int nbCores;
  private int nbThreads;

  public SimpleCollector getDefaultCollector() {
    return new SimpleTransientCollector(this, DEFAULT_CONFIG_KEY);
  }

  @Override
  public boolean changed(SimpleCollector c) {
    return true;
  }

  @Override
  public void setConfig(Map<String, String> config, Map<String, Map<String, String>> configs){

    computeNbCpus();

    if (!new File("/proc/").exists() && !new File("/sys/").exists()) {
      disabled = true;
      log(Level.WARNING, "Neither /proc nor /sys exists, disabling ProcGrabber.");
      return;
    }

    String sbd = config.get("blockDevices");
    if (sbd!=null) {
      blockDevices = Arrays.asList(sbd.split(","));
    }
    if (blockDevices == null) {
      autoDetectBlockDevices = true;
    }
    if (config.get("mdstatFile")!=null) {
      mdstatFile = new File(config.get("mdstatFile"));
      log(Level.INFO, "Using mdstat file from " + mdstatFile.getAbsolutePath());
    } else {
      mdstatFile = new File(MDSTAT_FILE);
    }
  }

  private void computeNbCpus() {
    if (nbCores == 0) {
      try (Stream<String> stream = Files.lines( Paths.get("/proc/cpuinfo"), StandardCharsets.UTF_8)) {
        stream.forEach((s) -> {
          try {
            if (s.startsWith("processor\t")) nbThreads++;
            if (s.startsWith("core id\t"))
              nbCores = Math.max(nbCores, Integer.parseInt(s.substring(s.lastIndexOf(' ') + 1)) + 1);
          } catch (Exception e) {
            log(Level.SEVERE, "Parsing /proc/cpuinfo, line: " + s, e);
          }
        });
        if (nbCores==0) nbCores=-1;
        if (nbThreads==0) nbThreads=-1;
      } catch (IOException e) {
        log(Level.SEVERE, "Grabbing /proc/cpuinfo", e);
      }
    }
  }

  private void extractBlockDevices() {
    if (!autoDetectBlockDevices) return;
    if (blockDeviceDetector==null) blockDeviceDetector = new ExternalBinaryRunner(new String[] {"lsblk", "-d","-o","NAME,MODEL"});
    if (blockDeviceDetector.exists()) {
      if (blockDeviceDetector.exec()) {
        byte[]out = blockDeviceDetector.getBuffer();
        int len = blockDeviceDetector.getLength();
        List<String> nbds = new ArrayList<>(blockDevices==null ? 10 : blockDevices.size()+10);
        byte c=0;
        int index=0;
        int status=0;
        int idxBegin=0;
        int idxEnd=0;
        while (c!='\n' && index<len) c = out[index++];
        idxBegin = index;
        while (true) {
          if (index == len) {
            c = '\n';
            index++;
          } else if (index > len) {
            if (blockDevices==null || !blockDevices.equals(nbds)) {
              blockDevices = nbds;
              blockDeviceSectorSize.clear();
              log(Level.INFO, "Block devices detected: " + blockDevices.stream().collect(Collectors.joining(",")));
            }
            return;
          } else {
            c = out[index++];
          }
          switch (status) {
            case -1:
              if (c == '\n') {
                idxBegin=index;
                status=0;
              }
              break;
            case 0:
              if (c == ' ') {
                idxEnd=index;
                status=1;
              }
              break;
            case 1:
              if (c == ' ') {
              } else if (c == '\n') {
                status=0;
                idxBegin=index;
              } else {
                status=-1;
                nbds.add(new String(out, idxBegin, idxEnd-idxBegin-1, StandardCharsets.UTF_8));
              }
              break;
          }
        }
      }
    } else {
      log(Level.WARNING, "Could not find lsblk, skipping block devices detection.");
      blockDevices = new ArrayList<>();
      blockDevices.add("sda");
      autoDetectBlockDevices = false;
    }
  }

  static FilenameFilter filter = new FilenameFilter() {
    @Override
    public boolean accept(File f, String str) {
      if (str == null) {
        return false;
      }
      int length = str.length();
      if (length == 0) {
        return false;
      }
      for (int i=0 ; i < length; i++) {
        char c = str.charAt(i);
        if (c < '0' || c > '9') {
          return false;
        }
      }
      return true;
    }
  };

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public synchronized void grabSync(SimpleCollector c) {
    if (disabled) {
      return;
    }
    extract(c,"processes", this::grabProcesses, CDuration.ZERO);
    extract(c,"uptime", this::grabUptimeAndLoad, CDuration.ZERO);
    extract(c,"cpu", this::grabCpuUsage, CDuration.ZERO);
    extract(c,"nbcpu", this::grabCpuNum, CDuration.ZERO);
    extract(c,"mem", this::grabMemUsage, CDuration.ZERO);
    extract(c,"bdio", this::grabBlockDeviceIo, CDuration.ZERO);
    extract(c,"net", this::getNetStats, CDuration.ZERO);
    extract(c,"battery", this::getBattery, CDuration.FIVE_SECONDS);
    extract(c,"mdstat", this::grabMdstat, CDuration.ONE_MINUTE);
    extract(c,"hostname", this::getHostname, CDuration.ONE_HOUR);

    if (canLogFiner()) {
      log(Level.FINER, "ProcGrabber usage: ");
      log(Level.FINER, "   lastProcessesCpuUsage: " + lastProcessesCpuUsage.size());
      log(Level.FINER, "            lastCpuUsage: " + lastCpuUsage.length);
      log(Level.FINER, "     lastBlockDeviceRead: " + lastBlockDeviceRead.size());
      log(Level.FINER, "    lastBlockDeviceWrite: " + lastBlockDeviceWrite.size());
      log(Level.FINER, "   blockDeviceSectorSize: " + blockDeviceSectorSize.size());
      log(Level.FINER, "            procStatFile: " + procStatFile.size());
      log(Level.FINER, "            blockDevices: " + blockDevices.size());
    }
  }

  private void grabCpuNum(SimpleCollector c) {
    c.collect("nbcpu_cores", nbCores);
    c.collect("nbcpu_threads", nbThreads);
  }

  private void grabMdstat(SimpleCollector c) {
    MdstatParser.MdstatResult data = MdstatParser.parseMdstat(mdstatFile);
    c.collect("mdstatFailed", data.getFailedDisks());
    c.collect("mdstatSummary", data.getOneline());
    c.collect("mdstatNbDevices", data.getIndividual().size());
    int i=0;
    for (String s : data.getIndividual())
      c.collect("mdstatByArray" + i++, s);
  }

  private void getBattery(SimpleCollector c) {
    if (BAT_present == null) {
      BAT_FULL = new File("/sys/class/power_supply/BAT0/charge_full");
      BAT_NOW = new File("/sys/class/power_supply/BAT0/charge_now");
      if (!BAT_FULL.exists() || !BAT_NOW.exists()) {
        if (!BAT_FULL.exists()) log(Level.INFO, BAT_FULL.getAbsolutePath() + " doesn't exist, skipping battery status extraction.");
        if (!BAT_NOW.exists()) log(Level.INFO, BAT_NOW.getAbsolutePath() + " doesn't exist, skipping battery status extraction.");
        BAT_present = false;
        return;
      }
      BAT_AC = new File("/sys/class/power_supply/AC/online");
      if (!BAT_AC.exists()) {
        log(Level.INFO, BAT_AC.getAbsolutePath() + " doesn't exist, trying alternative.");
        BAT_AC = new File("/sys/class/power_supply/AC0/online");
      }
      if (!BAT_AC.exists()) {
        log(Level.INFO, BAT_AC.getAbsolutePath() + " doesn't exist, skipping battery status extraction.");
        BAT_present = false;
        return;
      }

      BAT_present = true;
    }
    if (BAT_present) {
      if ((BAT_counter++%10)==0) {
        long full = PerformanceTools.parseLongInFile(BAT_FULL, babuffer1k);
        long now = PerformanceTools.parseLongInFile(BAT_NOW, babuffer1k);
        long plugged = PerformanceTools.parseLongInFile(BAT_AC, babuffer1k);
        c.collect("bat_exists", 1);
        c.collect("bat_prc", BAT_prc = now*100./full);
        c.collect("bat_plugged", BAT_plugged = plugged);
      } else {
        c.collect("bat_exists", 1);
        c.collect("bat_prc", BAT_prc);
        c.collect("bat_plugged", BAT_plugged);
      }
    } else {
      c.collect("bat_exists", 0);
    }
  }

  private void getHostname(SimpleCollector c) {
    try (Stream<String> stream = Files.lines( Paths.get("/etc/hostname"), StandardCharsets.UTF_8)) {
      c.collect("hostname", stream.findFirst().get());
    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing /etc/hostname", e);
    }
  }

  private void getNetStats(SimpleCollector c) {
    try (Stream<String> stream = Files.lines( Paths.get("/proc/net/netstat"), StandardCharsets.UTF_8)) {
      Iterator<String> it = stream.iterator();
      int status=0;
      boolean done=false;
      String line = null;
      while (it.hasNext()) {
        if (done) break;
        line = it.next();
        switch (status) {
          case 0:
            if (line.startsWith("IpExt:")) status++;
            break;
          case 1:
            if (line.startsWith("IpExt:")) done=true;
            break;
        }
      }
      if (done) {
        int2buffer[0] = 7;
        int2buffer[1] = 8;
        long2buffer[0]=long2buffer[1]=0l;
        PerformanceTools.parseLongInString(line, int2buffer, long2buffer);
        long in = long2buffer[0];
        long out = long2buffer[1];

        if (lastNin!=0) {
          computeAutoMaxPerSecond(c, "netp_in", in - lastNin);
          computeAutoMaxPerSecond(c, "netp_out", out - lastNout);
        }
        lastNin=in;
        lastNout=out;
      }

    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing /proc/net/netstat", e);
    }
  }

  private void grabBlockDeviceIo(SimpleCollector c) {
    extractBlockDevices();
    if (blockDevices.isEmpty()) return;
    if (blockDeviceSectorSize.size() != blockDevices.size()) {
      for (String bd : blockDevices) {
        try (Stream<String> stream = Files.lines( Paths.get("/sys/block/"+bd+"/queue/hw_sector_size"), StandardCharsets.UTF_8)) {
          String fl = stream.findFirst().get();
          blockDeviceSectorSize.put(bd, Integer.parseInt(fl));
        } catch (IOException e) {
          log(Level.SEVERE, "Grabbing sector size for " + bd, e);
        }
      }
    }
    long allRead=0, allWrite=0;
    StringBuilder allbd = new StringBuilder(blockDevices.size()*10);
    for (String bd : blockDevices) {
      if (allbd.length()>0) allbd.append(",");
      allbd.append(bd);
      int2buffer[0]=2;
      int2buffer[1]=6;
      long2buffer[0]=long2buffer[1]=0;
      PerformanceTools.parseLongsInFileFirstLine("/sys/block/"+bd+"/stat", babuffer1k, int2buffer, long2buffer);

      int ss = blockDeviceSectorSize.get(bd);
      long read = long2buffer[0]*ss;
      long write = long2buffer[1]*ss;
      long lastread = lastBlockDeviceRead.getOrDefault(bd,read);
      long lastwrite = lastBlockDeviceWrite.getOrDefault(bd,write);
      allRead += read - lastread;
      allWrite += write - lastwrite;
      c.collect("read_bytes_"+bd, (double)read - lastread);
      c.collect("write_bytes_"+bd, (double)write - lastwrite);
      computeAutoMaxPerSecond(c, "read_bytes_"+bd,(read - lastread));
      computeAutoMaxPerSecond(c, "write_bytes_"+bd,(write - lastwrite));
      lastBlockDeviceRead.put(bd,read);
      lastBlockDeviceWrite.put(bd,write);
    }
    computeAutoMaxPerSecond(c, "read_bytes_all",allRead);
    computeAutoMaxPerSecond(c, "write_bytes_all",allWrite);

    c.collect("read_bytes_all", (double)allRead);
    c.collect("write_bytes_all", (double)allWrite);
    c.collect("allbd", allbd.toString());
  }


  private void grabUptimeAndLoad(SimpleCollector c) {
    PerformanceTools.parseDoubleInFileFirstLine("/proc/uptime", babuffer1k, 1, double3buffer);
    c.collect("uptime", double3buffer[0]);
    PerformanceTools.parseDoubleInFileFirstLine("/proc/loadavg", babuffer1k, 3, double3buffer);
    c.collect("loadavg1", double3buffer[0]);
    c.collect("loadavg2", double3buffer[1]);
    c.collect("loadavg3", double3buffer[2]);
  }

  private void grabMemUsage(SimpleCollector c) {
    try (Stream<String> stream = Files.lines( Paths.get("/proc/meminfo"), StandardCharsets.UTF_8)) {
      double ramTotal=0,ramFree=0,ramCached=0,ramBuffers=0,swapTotal=0,swapFree=0,kReclaimable=0;
      int searchFor = 7;
      Iterator<String> it = stream.iterator();
      while (it.hasNext()) {
        String line = it.next();
        if (ramTotal==0 && line.startsWith("MemTotal:")) { searchFor--; ramTotal = parseMemLine(line);} else
        if (ramFree==0 && line.startsWith("MemFree:")) { searchFor--; ramFree = parseMemLine(line);} else
        if (ramBuffers==0 && line.startsWith("Buffers:")) { searchFor--; ramBuffers = parseMemLine(line);} else
        if (ramCached==0 && line.startsWith("Cached:")) { searchFor--; ramCached = parseMemLine(line);} else
        if (swapTotal==0 && line.startsWith("SwapTotal:")) { searchFor--; swapTotal = parseMemLine(line);} else
        if (swapFree==0 && line.startsWith("SwapFree:")) { searchFor--; swapFree = parseMemLine(line);}
        if (kReclaimable==0 && line.startsWith("KReclaimable:")) { searchFor--; kReclaimable = parseMemLine(line);}
        if (searchFor==0) break;
      }

      c.collect("ramTotal", ramTotal*1024);
      c.collect("ramFree", ramFree*1024);
      c.collect("ramUsed", (ramTotal-ramFree-ramBuffers-ramCached-kReclaimable)*1024);
      c.collect("ramCached", (ramCached+ramBuffers+kReclaimable)*1024);
      c.collect("swapTotal", swapTotal*1024);
      c.collect("swapUsed", (swapTotal-swapFree)*1024);
      c.collect("swapFree", swapFree*1024);

    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing /proc/meminfo", e);
    }
  }

  private double parseMemLine(String line) { // Parse the number out of a line like "SReclaimable:    1542216 kB"
    int p = line.indexOf(':');
    line = line.substring(p+1).trim();
    p = line.indexOf(' ');
    return Double.parseDouble(line.substring(0, p));
  }

  private void grabCpuUsage(SimpleCollector c) {
    try (Stream<String> stream = Files.lines( Paths.get("/proc/stat"), StandardCharsets.UTF_8)) {
      String fl = stream.findFirst().get().replaceAll("[ ]+", " ");
      String[]ss = fl.split(" ");
      double[]cpu = new double[]{
        Double.parseDouble(ss[1]),
        Double.parseDouble(ss[2]),
        Double.parseDouble(ss[3]),
        Double.parseDouble(ss[4]),
        Double.parseDouble(ss[5]),
        Double.parseDouble(ss[6]),
        Double.parseDouble(ss[7])
      };
      double allcpu = cpu[0]+cpu[1]+cpu[2]+cpu[3]+cpu[4]+cpu[5]+cpu[6]-lastCpuUsage[0]-lastCpuUsage[1]-lastCpuUsage[2]-lastCpuUsage[3]-lastCpuUsage[4]-lastCpuUsage[5]-lastCpuUsage[6];
      c.collect("cpu_usg_user", (cpu[0]-lastCpuUsage[0])*100/allcpu);
      c.collect("cpu_usg_nice", (cpu[1]-lastCpuUsage[1])*100/allcpu);
      c.collect("cpu_usg_sys", (cpu[2]-lastCpuUsage[2])*100/allcpu);
      c.collect("cpu_usg_idle", (cpu[3]-lastCpuUsage[3])*100/allcpu);
      c.collect("cpu_usg_used", 100-(cpu[3]-lastCpuUsage[3])*100/allcpu);
      c.collect("cpu_usg_wait", (cpu[4]+cpu[5]+cpu[6]-lastCpuUsage[4]-lastCpuUsage[5]-lastCpuUsage[6])*100/allcpu);
      lastCpuUsage = cpu;
    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing /proc/stat", e);
    }
  }

  private void grabProcesses(SimpleCollector c) {
    try {
      long curTs = System.currentTimeMillis();

      String[] list = new File("/proc/").list(filter);
      double totalcpu = 0;
      double totalmem = 0;
      ProcessStat[]top3cpu = new ProcessStat[]{
          ProcessStat.get(0,0,0,0,null,0,0),
          ProcessStat.get(0,0,0,0,null,0,0),
          ProcessStat.get(0,0,0,0,null,0,0)
      };
      ProcessStat[]top3mem = new ProcessStat[]{
          ProcessStat.get(0,0,0,0,null,0,0),
          ProcessStat.get(0,0,0,0,null,0,0),
          ProcessStat.get(0,0,0,0,null,0,0)
      };
      for (String f : list) {
        try {
          long pid = Long.parseLong(f);
          ProcessStat ps = loadProcessStats(pid, curTs);
          totalcpu += ps.getTotalCpuDiff();
          totalmem += ps.getMem();
          if (ps.getTotalCpuDiff()>top3cpu[2].getTotalCpuDiff()) {
            top3cpu[0] = top3cpu[1];
            top3cpu[1] = top3cpu[2];
            top3cpu[2] = ps;
          } else if (ps.getTotalCpuDiff()>top3cpu[1].getTotalCpuDiff()) {
            top3cpu[0] = top3cpu[1];
            top3cpu[1] = ps;
          } else if (ps.getTotalCpuDiff()>top3cpu[0].getTotalCpuDiff()) {
            top3cpu[0] = ps;
          }
          if (ps.getMem()>top3mem[2].getMem()) {
            top3mem[0] = top3mem[1];
            top3mem[1] = top3mem[2];
            top3mem[2] = ps;
          } else if (ps.getMem()>top3mem[1].getMem()) {
            top3mem[0] = top3mem[1];
            top3mem[1] = ps;
          } else if (ps.getMem()>top3mem[0].getMem()) {
            top3mem[0] = ps;
          }
        } catch (Exception e) {
          // Process might be dead since the listing.
        }
      }
      c.collect("prc_top1cpu", top3cpu[2].getTotalCpuDiff()*100. / totalcpu);
      c.collect("prc_top2cpu", top3cpu[1].getTotalCpuDiff()*100. / totalcpu);
      c.collect("prc_top3cpu", top3cpu[0].getTotalCpuDiff()*100. / totalcpu);
      c.collect("top1cpu", top3cpu[2].getName());
      c.collect("top2cpu", top3cpu[1].getName());
      c.collect("top3cpu", top3cpu[0].getName());
      c.collect("pid_top1cpu", ""+top3cpu[2].getPid());
      c.collect("pid_top2cpu", ""+top3cpu[1].getPid());
      c.collect("pid_top3cpu", ""+top3cpu[0].getPid());

      c.collect("prc_top1mem", top3mem[2].getMem()*100. / totalmem);
      c.collect("prc_top2mem", top3mem[1].getMem()*100. / totalmem);
      c.collect("prc_top3mem", top3mem[0].getMem()*100. / totalmem);
      c.collect("top1mem", top3mem[2].getName());
      c.collect("top2mem", top3mem[1].getName());
      c.collect("top3mem", top3mem[0].getName());
      c.collect("pid_top1mem", ""+top3mem[2].getPid());
      c.collect("pid_top2mem", ""+top3mem[1].getPid());
      c.collect("pid_top3mem", ""+top3mem[0].getPid());

    } finally {
      ProcessStat.releaseAll();
      if (procStatFileLimit>0) {
        if (procStatFile.size()>procStatFileLimit) {
          if (canLogFine()) log(Level.FINE, "GCing PSF " + procStatFile.size());
          gcProcStatFile();
          procStatFileLimit = 0;
        }
      }
      if (procStatFileLimit==0) {
        procStatFileLimit = procStatFile.size()*2;
      }
    }
  }

  private void gcProcStatFile() {
    Set<Long> expiredKeys = new HashSet<>(procStatFile.size()/2);
    long now = System.currentTimeMillis();

    for (Map.Entry<Long, ProcStatHelper> e : procStatFile.entrySet()) {
      if (now-e.getValue().lastUsed>2000) expiredKeys.add(e.getKey());
    }

    if (canLogFine()) log(Level.FINE, "GC procStatFile by removing " + expiredKeys.size() + " out of " + procStatFile.size() + " entries.");
    expiredKeys.forEach(k -> procStatFile.remove(k));
    Map<Long, Long> lpcu = HashMapPool.getInstance().borrow(procStatFile.size());
    procStatFile.keySet().forEach(k -> lpcu.put(k, lastProcessesCpuUsage.get(k)));
    Map tmp = lastProcessesCpuUsage;
    lastProcessesCpuUsage = lpcu;
    HashMapPool.getInstance().giveBack(tmp);
  }

  private File getStatFileForProcess(Long pid, long now) {
    ProcStatHelper psh = procStatFile.get(pid);
    if (psh == null) {
      psh = new ProcStatHelper();
      psh.pid=pid;
      psh.file = new File("/proc/"+pid+"/stat");
      //System.out.print("/");
      procStatFile.put(pid, psh);
    }
    psh.lastUsed=now;
    return psh.file;
  }

  private ProcessStat loadProcessStats(Long pid, long now) throws IOException {
    ProcessStat ps = PerformanceTools.parseProcessStatFile(getStatFileForProcess(pid, now), pid, babuffer1k);
    if (ps!=null) {
      long t = ps.getUtime() + ps.getStime();
      double v = t - lastProcessesCpuUsage.getOrDefault(pid, 0l);
      lastProcessesCpuUsage.put(pid, t);
      ps.setTotalCpuDiff(v);
    }
    return ps;
  }
}


