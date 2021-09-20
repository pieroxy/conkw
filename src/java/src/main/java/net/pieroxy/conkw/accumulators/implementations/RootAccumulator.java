package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.util.Map;

public class RootAccumulator<T extends LogRecord> implements Accumulator<T> {
  public static final String NAME = "root";

  private final String rootName;
  final Accumulator<T> accumulator;

  public RootAccumulator(String rootName, Accumulator<T> acc) {
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
    accumulator.log(prefix, data, str);
  }

  @Override
  public synchronized void reset() {
    accumulator.reset();
  }
}
