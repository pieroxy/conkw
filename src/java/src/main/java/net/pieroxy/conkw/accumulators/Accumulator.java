package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

/**
 * This interface defines the behavior of an accumulator. An accumulator will accumulate (and aggregate) data
 * and will then be able to log its data on the Maps provided.
 */
public interface Accumulator<T extends DataRecord> {
  double add(T line);

  /**
   * Implementations are only expected to sum with another instance of themselves. They may throw if
   * an instance of another class is provided. A ClassCastException for example :)
   * This holds true for the whole set of accumulators this accumulator might hold.
   * This function takes the currently stable set of metrics and injects it in the current set of metrics.
   * @param acc
   */
  void sumWith(Accumulator<T> acc);
  void initializeFromData(PrefixedDataRecord record);
  double getTotal();
  void prepareNewSession();
  void log(String prefix, DataRecord record);
}
