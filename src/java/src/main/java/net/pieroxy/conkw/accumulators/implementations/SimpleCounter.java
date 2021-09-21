package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;

import java.util.Map;

public class SimpleCounter<T extends LogRecord> implements Accumulator<T> {
  public static final String NAME = "count";
  int count;
  int oldCount;

  @Override
  public String toString() {
    return "SimpleCounter{" +
            "count=" + count +
            ", oldCount=" + oldCount +
            '}';
  }

  @Override
  public double add(LogRecord line) {
    count++;
    return 1d;
  }

  @Override
  public void log(String prefix, Map<String, Double> data, Map<String, String> str) {
    data.put(AccumulatorUtils.addToMetricName(prefix, NAME), (double)oldCount);
  }

  @Override
  public void sumWith(Accumulator acc) {
    oldCount += ((SimpleCounter)acc).oldCount;
  }

  @Override
  public double getTotal() {
    return oldCount;
  }

  @Override
  public void prepareNewSession() {
    oldCount = count;
    count = 0;
  }
}
