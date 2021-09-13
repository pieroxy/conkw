package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

public interface AccumulatorProvider<T extends LogRecord> {
  Accumulator<T> getAccumulator() throws Exception;
}
