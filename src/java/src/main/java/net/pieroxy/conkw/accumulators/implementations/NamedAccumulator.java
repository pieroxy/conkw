package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

public class NamedAccumulator<T extends DataRecord> implements Accumulator<T> {
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
  public void initializeFromData(PrefixedDataRecord record) {
    record.pushPrefix(rootName + ".");
    accumulator.initializeFromData(record);
    record.popPrefix();
  }

  @Override
  public double getTotal() {
    return accumulator.getTotal();
  }

  @Override
  public synchronized void log(String prefix, DataRecord record) {
    accumulator.log(AccumulatorUtils.addToMetricName(prefix, rootName), record);
  }

  @Override
  public synchronized void prepareNewSession() {
    accumulator.prepareNewSession();
  }
}
