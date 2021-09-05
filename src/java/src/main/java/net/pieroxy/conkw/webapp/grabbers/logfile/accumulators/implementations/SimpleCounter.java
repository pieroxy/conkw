package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.Accumulator;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.AccumulatorUtils;

import java.io.PrintStream;
import java.util.Map;

public class SimpleCounter<T extends LogRecord> implements Accumulator<T> {
  public static final String NAME = "count";
  int count;

  @Override
  public void add(LogRecord line) {
    count++;
  }

  @Override
  public void log(String prefix, Map<String, Double> data) {
    data.put(AccumulatorUtils.addToMetricName(prefix, NAME), (double)count);
  }

  @Override
  public void reset() {
    count = 0;
  }
}
