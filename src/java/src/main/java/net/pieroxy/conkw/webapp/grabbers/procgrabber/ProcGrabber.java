package net.pieroxy.conkw.webapp.grabbers.procgrabber;

import net.pieroxy.conkw.utils.PerformanceTools;
import net.pieroxy.conkw.webapp.grabbers.AsyncGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

public class ProcGrabber extends AsyncGrabber {
  static final String NAME = "proc";

  private Map<Long,Long> lastProcessesCpuUsage = new HashMap<>();
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

  @Override
  public boolean changed() {
    return true;
  }

  @Override
  public void setConfig(Map<String, String> config){
    blockDevices = Arrays.asList(config.get("blockDevices").split(","));
    if (blockDevices == null) {
      blockDevices = new ArrayList<>();
      blockDevices.add("sda");
    }
  }

  @Override
  public void writeHtmlTemplate(Writer writer) throws IOException {
    String namespaceAttr = "cw-ns=\""+getName()+"\"";
    InputStream is = getClass().getClassLoader().getResourceAsStream("procgrabber.template.html");
    BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF8")));
    String line;
    while ((line = br.readLine())!=null) {
      writer.write(line.replaceAll("cw-ns=\"proc\"", namespaceAttr));
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
  public void processHttp(HttpServletRequest req) {
  }

  @Override
  public String getDefaultName() {
    return NAME;
  }

  @Override
  public synchronized ResponseData grabSync() {
    ResponseData r = new ResponseData(getName(), System.currentTimeMillis());
    if (shouldExtract("processes")) grabProcesses(r);
    if (shouldExtract("uptime")) grabUptimeAndLoad(r);
    if (shouldExtract("cpu")) grabCpuUsage(r);
    if (shouldExtract("mem")) grabMemUsage(r);
    if (shouldExtract("bdio")) grabBlockDeviceIo(r);
    if (shouldExtract("net")) getNetStats(r);
    if (shouldExtract("hostname")) getHostname(r);
    if (shouldExtract("battery")) getBattery(r);

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

    return r;
  }

  private void getBattery(ResponseData r) {
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
        r.addMetric("bat_exists", 1);
        r.addMetric("bat_prc", BAT_prc = now*100./full);
        r.addMetric("bat_plugged", BAT_plugged = plugged);
      } else {
        r.addMetric("bat_exists", 1);
        r.addMetric("bat_prc", BAT_prc);
        r.addMetric("bat_plugged", BAT_plugged);
      }
    } else {
      r.addMetric("bat_exists", 0);
    }
  }

  private void getHostname(ResponseData r) {
    try (Stream<String> stream = Files.lines( Paths.get("/etc/hostname"), StandardCharsets.UTF_8)) {
      r.addMetric("hostname", stream.findFirst().get());
    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing /etc/hostname", e);
    }
  }

  private void getNetStats(ResponseData r) {
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
          r.addMetric("netp_in", (double) (in - lastNin));
          r.addMetric("netp_out", (double) (out - lastNout));
        }
        lastNin=in;
        lastNout=out;
      }

    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing /proc/net/netstat", e);
    }
  }

  private void grabBlockDeviceIo(ResponseData r) {
    if (blockDevices.isEmpty()) return;
    if (blockDeviceSectorSize.isEmpty()) {
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
    for (String bd : blockDevices) {
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
      r.addMetric("read_bytes_"+bd, (double)read - lastread);
      r.addMetric("write_bytes_"+bd, (double)write - lastwrite);
      lastBlockDeviceRead.put(bd,read);
      lastBlockDeviceWrite.put(bd,write);
    }
    r.addMetric("read_bytes_all", (double)allRead);
    r.addMetric("write_bytes_all", (double)allWrite);
  }


  private void grabUptimeAndLoad(ResponseData r) {
    PerformanceTools.parseDoubleInFileFirstLine("/proc/uptime", babuffer1k, 1, double3buffer);
    r.addMetric("uptime", double3buffer[0]);
    PerformanceTools.parseDoubleInFileFirstLine("/proc/loadavg", babuffer1k, 3, double3buffer);
    r.addMetric("loadavg1", double3buffer[0]);
    r.addMetric("loadavg2", double3buffer[1]);
    r.addMetric("loadavg3", double3buffer[2]);
  }

  private void grabMemUsage(ResponseData r) {
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

      r.addMetric("ramTotal", ramTotal*1024);
      r.addMetric("ramFree", ramFree*1024);
      r.addMetric("ramUsed", (ramTotal-ramFree-ramBuffers-ramCached-kReclaimable)*1024);
      r.addMetric("ramCached", (ramCached+ramBuffers+kReclaimable)*1024);
      r.addMetric("swapTotal", swapTotal*1024);
      r.addMetric("swapUsed", (swapTotal-swapFree)*1024);
      r.addMetric("swapFree", swapFree*1024);

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

  private void grabCpuUsage(ResponseData r) {
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
      r.addMetric("cpu_usg_user", (cpu[0]-lastCpuUsage[0])*100/allcpu);
      r.addMetric("cpu_usg_nice", (cpu[1]-lastCpuUsage[1])*100/allcpu);
      r.addMetric("cpu_usg_sys", (cpu[2]-lastCpuUsage[2])*100/allcpu);
      r.addMetric("cpu_usg_idle", (cpu[3]-lastCpuUsage[3])*100/allcpu);
      r.addMetric("cpu_usg_used", 100-(cpu[3]-lastCpuUsage[3])*100/allcpu);
      r.addMetric("cpu_usg_wait", (cpu[4]+cpu[5]+cpu[6]-lastCpuUsage[4]-lastCpuUsage[5]-lastCpuUsage[6])*100/allcpu);
      lastCpuUsage = cpu;
    } catch (IOException e) {
      log(Level.SEVERE, "Grabbing /proc/stat", e);
    }
  }

  private void grabProcesses(ResponseData r) {
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
      r.addMetric("prc_top1cpu", top3cpu[2].getTotalCpuDiff()*100. / totalcpu);
      r.addMetric("prc_top2cpu", top3cpu[1].getTotalCpuDiff()*100. / totalcpu);
      r.addMetric("prc_top3cpu", top3cpu[0].getTotalCpuDiff()*100. / totalcpu);
      r.addMetric("top1cpu", top3cpu[2].getName());
      r.addMetric("top2cpu", top3cpu[1].getName());
      r.addMetric("top3cpu", top3cpu[0].getName());
      r.addMetric("pid_top1cpu", ""+top3cpu[2].getPid());
      r.addMetric("pid_top2cpu", ""+top3cpu[1].getPid());
      r.addMetric("pid_top3cpu", ""+top3cpu[0].getPid());

      r.addMetric("prc_top1mem", top3mem[2].getMem()*100. / totalmem);
      r.addMetric("prc_top2mem", top3mem[1].getMem()*100. / totalmem);
      r.addMetric("prc_top3mem", top3mem[0].getMem()*100. / totalmem);
      r.addMetric("top1mem", top3mem[2].getName());
      r.addMetric("top2mem", top3mem[1].getName());
      r.addMetric("top3mem", top3mem[0].getName());
      r.addMetric("pid_top1mem", ""+top3mem[2].getPid());
      r.addMetric("pid_top2mem", ""+top3mem[1].getPid());
      r.addMetric("pid_top3mem", ""+top3mem[0].getPid());

    } finally {
      ProcessStat.releaseAll();
      if (procStatFileLimit>0) {
        if (procStatFile.size()>procStatFileLimit) {
          gcProcStatFile();
          procStatFileLimit = 0;
        }
      }
      if (procStatFileLimit==0) {
        procStatFileLimit = procStatFile.size()*4;
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
    Map<Long, Long> lpcu = new HashMap<>(procStatFile.size());
    procStatFile.keySet().forEach(k -> lpcu.put(k, lastProcessesCpuUsage.get(k)));
    lastProcessesCpuUsage = lpcu;
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


class ProcStatHelper {
  long pid;
  long lastUsed;
  File file;
}