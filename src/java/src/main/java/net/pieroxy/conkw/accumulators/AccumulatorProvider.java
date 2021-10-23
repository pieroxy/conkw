package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.pub.mdlog.LogRecord;

public interface AccumulatorProvider<T extends LogRecord> {
  Accumulator<T> getAccumulator() throws Exception;
}
