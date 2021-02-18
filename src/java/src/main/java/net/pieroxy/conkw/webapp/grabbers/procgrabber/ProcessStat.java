package net.pieroxy.conkw.webapp.grabbers.procgrabber;

import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class ProcessStat {
  private static Stack<ProcessStat> free = new Stack<>();
  private static Stack<ProcessStat> busy = new Stack<>();
  public static ProcessStat get(long mem, long pid, long utime, long stime, byte[] name, int nameOffset, int nameLen) {
    if (free.isEmpty()) {
      free.add(new ProcessStat());
      //System.out.print(".");
    }
    ProcessStat res = free.pop();
    res.init(mem, pid, utime, stime, name, nameOffset, nameLen);
    busy.add(res);
    return res;
  }

  private ProcessStat() {
  }

  public static void releaseAll() {
    while (!busy.empty()) free.add(busy.pop());
    busy.clear();
  }


  private double totalCpuDiff;
  private long mem;
  private long pid;
  private long utime;
  private long stime;
  private byte[]name = new byte[100];
  private int namelen;

  public ProcessStat init(long mem, long pid, long utime, long stime, byte[] name, int nameOffset, int nameLen) {
    this.totalCpuDiff=0;
    this.mem = mem;
    this.pid = pid;
    this.utime = utime;
    this.stime = stime;
    if (name!=null) {
      namelen = Math.min(this.name.length, nameLen);
      System.arraycopy(name, nameOffset, this.name, 0, namelen);
    } else {
      namelen=0;
    }
    return this;
  }

  public double getTotalCpuDiff() {
    return totalCpuDiff;
  }

  public void setTotalCpuDiff(double totalCpuDiff) {
    this.totalCpuDiff = totalCpuDiff;
  }

  public long getMem() {
    return mem;
  }

  public long getPid() {
    return pid;
  }

  public long getUtime() {
    return utime;
  }

  public long getStime() {
    return stime;
  }

  public String getName() {
    return name==null ? "" : new String(name, 0, namelen, StandardCharsets.UTF_8);
  }
}