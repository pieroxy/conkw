package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

public interface AccumulatorProvider<T extends LogRecord> {
  Accumulator<T> getAccumulator() throws Exception;
}
