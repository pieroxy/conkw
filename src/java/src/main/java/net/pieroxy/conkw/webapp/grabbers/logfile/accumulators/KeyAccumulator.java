package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations.SimpleCounter;
import org.apache.catalina.valves.rewrite.Substitution;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This abstract class will allow aggregations around a key and the number of occurrence of said key.
 * @param <A> The type of the key being considered.
 */
public abstract class KeyAccumulator<A, T extends LogRecord> implements Accumulator<T> {
  private final static Logger LOGGER = Logger.getLogger(KeyAccumulator.class.getName());
  private final AccumulatorProvider<T> provider;
  Map<A, Accumulator<T>> data = new HashMap<>();

  abstract public A getKey(T record);

  public KeyAccumulator(AccumulatorProvider<T> provider) {
    this.provider = provider;
  }

  @Override
  public void add(T line) {
    A key = getKey(line);
    if (key == null) return;

    Accumulator<T> acc = data.getOrDefault(key, null);
    if (acc==null) try {
      data.put(key, acc = provider.getAccumulator());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Cannot create accumulator", e);
    }
    acc.add(line);
  }

  @Override
  public void reset() {
    data.clear();
  }

  @Override
  public void log(String prefix, Map<String, Double> num) {
    for (Map.Entry<A, Accumulator<T>> entry : data.entrySet()) {
      entry.getValue().log(AccumulatorUtils.addToMetricName(prefix, String.valueOf(entry.getKey())), num);
    }
  }
}
