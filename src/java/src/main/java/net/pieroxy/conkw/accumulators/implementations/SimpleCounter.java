package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

public class SimpleCounter<T extends DataRecord> implements Accumulator<T> {
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
  public double add(DataRecord line) {
    count++;
    return 1d;
  }

  @Override
  public void log(String prefix, DataRecord record) {
    record.getValues().put(AccumulatorUtils.addToMetricName(prefix, NAME), (double)oldCount);
  }

  @Override
  public Accumulator<T> getFreshInstance() {
    return new SimpleCounter<T>();
  }

  @Override
  public void sumWith(Accumulator acc) {
    count += ((SimpleCounter)acc).oldCount;
  }

  @Override
  public void initializeFromData(PrefixedDataRecord record) {
    Double val = record.getValues().get(NAME);
    count = val==null ? 0 : (int)(double)val;
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
