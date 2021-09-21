package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.util.Map;

public class RootAccumulator<T extends LogRecord> implements Accumulator<T> {
  public static final String NAME = "root";
  public static final String ELAPSED = "elapsed";

  private final String rootName;
  final Accumulator<T> accumulator;
  long lastResetTime;
  long lastPeriod = 0;

  public RootAccumulator(String rootName, Accumulator<T> acc) {
    this.rootName = rootName;
    accumulator = acc;
    lastResetTime = System.currentTimeMillis();
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
    data.put(AccumulatorUtils.addToMetricName(prefix, ELAPSED), (double)lastPeriod);
    accumulator.log(prefix, data, str);
  }

  @Override
  public synchronized void prepareNewSession() {
    accumulator.prepareNewSession();
    long now = System.currentTimeMillis();
    lastPeriod = now - lastResetTime;
    lastResetTime = now;
  }
}
