package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.pub.mdlog.LogRecord;

import java.util.Map;

/**
 * This interface defines the behavior of an accumulator. An accumulator will accumulate (and aggregate) data
 * and will then be able to log its data on the Maps provided.
 */
public interface Accumulator<T extends LogRecord> {
  double add(T line);

  /**
   * Implementations are only expected to sum with another instance of themselves. They may throw if
   * an instance of another class is provided. A ClassCastException for example :)
   * This holds true for the whole set of accumulators this accumulator might hold.
   * @param acc
   */
  void sumWith(Accumulator acc);
  double getTotal();
  void prepareNewSession();
  void log(String prefix, Map<String, Double> num, Map<String, String> str);

}
