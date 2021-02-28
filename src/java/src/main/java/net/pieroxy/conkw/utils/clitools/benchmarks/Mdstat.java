package net.pieroxy.conkw.utils.clitools.benchmarks;

import net.pieroxy.conkw.webapp.grabbers.procgrabber.MdstatParser;

import java.io.File;

public class Mdstat {
  public static void main(String args[]) {
    int iterations = Integer.parseInt(args[0]);
    File f = new File(args[1]);
    StringBuilder res = new StringBuilder();
    byte[]bab = new byte[10000];

    BenchmarkTool.bench("original", () -> MdstatParser.parseMdstat(f).getFailedDisks(), iterations);
    BenchmarkTool.bench("original", () -> MdstatParser.parseMdstat(f).getFailedDisks(), iterations);
  }
}
