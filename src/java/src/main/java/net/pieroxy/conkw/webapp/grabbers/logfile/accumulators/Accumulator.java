package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.io.PrintStream;
import java.util.Map;

/**
 * This interface defines the behavior of an accumulator. An accumulator will accumulate (and aggregate) data
 * and will then be able to log its data on the PrintStream provided.
 */
public interface Accumulator<T extends LogRecord> {
  double add(T line);

  /**
   * Implementations are only expected to sum with another instance of themselves. Thay may throw if
   * an instance of another class is provided. A ClassCastException for example :)
   * This holds true for the whole grap of accumulators this accumulator might hold.
   * @param acc
   */
  void sumWith(Accumulator acc);
  double getTotal();
  void reset();
  void log(String prefix, Map<String, Double> num, Map<String, String> str);

}
