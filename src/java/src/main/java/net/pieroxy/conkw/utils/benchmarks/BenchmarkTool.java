package net.pieroxy.conkw.utils.benchmarks;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;

public class BenchmarkTool {
  static long hs = Runtime.getRuntime().totalMemory();
  public static void bench(String name, Benchmarkable r, int iterations) {
    long start = System.nanoTime();
    int count=0;
    GCCH a = new GCCH();
    GCCH b = new GCCH();
    System.gc();
    fillGcc(a);
    for (int i=0 ; i<iterations ; i++) {
      count += r.run();
    }
    fillGcc(b);
    long end = System.nanoTime();
    long hss = Runtime.getRuntime().totalMemory();
    System.out.println(hss + "// name:" + name + " count=" + count + " ms: " + (end-start)/1000000. + " GC:" + (b.count-a.count) + " GC time:" + (b.time-a.time));
    if (hss!=hs) System.out.println("Heap size has changed. Expect garbage results. " + hs + " >> " + hss);
  }

  private static void fillGcc(GCCH res) {
    for(GarbageCollectorMXBean gc :
        ManagementFactory.getGarbageCollectorMXBeans()) {

      long count = gc.getCollectionCount();

      if(count >= 0) {
        res.count += count;
      }

      long time = gc.getCollectionTime();

      if(time >= 0) {
        res.time += time;
      }
    }
  }


}
