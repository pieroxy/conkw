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
  public void sumWith(Accumulator acc) {
    oldCount += ((SimpleCounter)acc).oldCount;
  }

  @Override
  public void initializeFromData(PrefixedDataRecord record) {
    count = (int)(double)record.getValues().get(NAME);
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
