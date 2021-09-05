package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.io.PrintStream;
import java.util.Map;

public class NamedAccumulator<T extends LogRecord> implements Accumulator<T> {
  public static final String NAME = "name";

  private final String rootName;
  final Accumulator<T> accumulator;

  public NamedAccumulator(String rootName, Accumulator<T> acc) {
    this.rootName = rootName;
    accumulator = acc;
  }

  public Accumulator<T> getAccumulator() {
    return accumulator;
  }

  public String getRootName() {
    return rootName;
  }

  @Override
  public synchronized void add(T line) {
    accumulator.add(line);
  }

  @Override
  public synchronized void log(String prefix, Map<String, Double> data) {
    accumulator.log(AccumulatorUtils.addToMetricName(prefix, rootName), data);
  }

  @Override
  public synchronized void reset() {
    accumulator.reset();
  }
}
