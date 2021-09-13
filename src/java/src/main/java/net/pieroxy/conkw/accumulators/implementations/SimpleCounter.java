package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;

import java.util.Map;

public class SimpleCounter<T extends LogRecord> implements Accumulator<T> {
  public static final String NAME = "count";
  int count;

  @Override
  public double add(LogRecord line) {
    count++;
    return 1d;
  }

  @Override
  public void log(String prefix, Map<String, Double> data, Map<String, String> str) {
    data.put(AccumulatorUtils.addToMetricName(prefix, NAME), (double)count);
  }

  @Override
  public void sumWith(Accumulator acc) {
    count += ((SimpleCounter)acc).count;
  }

  @Override
  public double getTotal() {
    return count;
  }

  @Override
  public void reset() {
    count = 0;
  }
}
