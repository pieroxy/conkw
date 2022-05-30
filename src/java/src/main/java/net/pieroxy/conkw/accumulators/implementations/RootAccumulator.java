package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

public class RootAccumulator<T extends DataRecord> implements Accumulator<T> {
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
    accumulator.sumWith(((RootAccumulator)acc).getAccumulator());
  }

  @Override
  public void initializeFromData(PrefixedDataRecord record) {
    Double el = record.getValues().get(ELAPSED);
    if (el == null) return; // No data to initialize
    lastPeriod = (long)(double)el;
    accumulator.initializeFromData(record);
  }

  @Override
  public double getTotal() {
    return accumulator.getTotal();
  }

  @Override
  public synchronized void log(String prefix, DataRecord record) {
    record.getValues().put(AccumulatorUtils.addToMetricName(prefix, ELAPSED), (double)lastPeriod);
    accumulator.log(prefix, record);
  }

  @Override
  public synchronized void prepareNewSession() {
    accumulator.prepareNewSession();
    long now = System.currentTimeMillis();
    lastPeriod = now - lastResetTime;
    lastResetTime = now;
  }

  public long getLastPeriod() {
    return lastPeriod;
  }

  public Accumulator<T> getFreshInstance() {
    return new RootAccumulator<T>(rootName, accumulator.getFreshInstance());
  }
}
