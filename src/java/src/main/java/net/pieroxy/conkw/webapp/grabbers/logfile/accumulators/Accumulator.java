package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.io.PrintStream;
import java.util.Map;

/**
 * This interface defines the behavior of an accumulator. An accumulator will accumulate (and aggregate) data
 * and will then be able to log its data on the PrintStream provided.
 */
public interface Accumulator<T extends LogRecord> {
  void add(T line);
  void reset();
  void log(String prefix, Map<String, Double> num);
}
