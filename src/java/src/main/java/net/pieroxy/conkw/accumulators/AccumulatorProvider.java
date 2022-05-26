package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.pub.mdlog.DataRecord;

public interface AccumulatorProvider<T extends DataRecord> {
  Accumulator<T> getAccumulator() throws Exception;
}
