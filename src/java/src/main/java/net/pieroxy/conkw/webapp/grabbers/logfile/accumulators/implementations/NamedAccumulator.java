package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.Accumulator;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.AccumulatorUtils;

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
  public synchronized double add(T line) {
    return accumulator.add(line);
  }

  @Override
  public void sumWith(Accumulator acc) {
    accumulator.sumWith(((NamedAccumulator)acc).getAccumulator());
  }

  @Override
  public double getTotal() {
    return accumulator.getTotal();
  }

  @Override
  public synchronized void log(String prefix, Map<String, Double> data, Map<String, String> str) {
    accumulator.log(AccumulatorUtils.addToMetricName(prefix, rootName), data, str);
  }

  @Override
  public synchronized void reset() {
    accumulator.reset();
  }
}
