package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This abstract class will allow aggregations around a key and the number of occurrence of said key.
 * @param <A> The type of the key being considered.
 */
public abstract class KeyAccumulator<A, T extends LogRecord> implements Accumulator<T> {
  private final static Logger LOGGER = Logger.getLogger(KeyAccumulator.class.getName());
  private final AccumulatorProvider<T> provider;
  private double total;
  Map<A, Accumulator<T>> data = new HashMap<>();

  abstract public A getKey(T record);

  public KeyAccumulator(AccumulatorProvider<T> provider) {
    this.provider = provider;
  }

  @Override
  public double getTotal() {
    return total;
  }

  @Override
  public double add(T line) {
    A key = getKey(line);
    if (key == null) return 0;

    Accumulator<T> acc = data.getOrDefault(key, null);
    if (acc==null) try {
      data.put(key, acc = provider.getAccumulator());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Cannot create accumulator", e);
    }
    double value = acc.add(line);
    total += value;
    return value;
  }

  @Override
  public void reset() {
    data.clear();
    total = 0;
  }

  @Override
  public void log(String prefix, Map<String, Double> num, Map<String, String> str) {
    for (Map.Entry<A, Accumulator<T>> entry : data.entrySet()) {
      entry.getValue().log(AccumulatorUtils.addToMetricName(prefix, String.valueOf(entry.getKey())), num, str);
    }
    num.put(AccumulatorUtils.addToMetricName(prefix, "total"), total);
    List<A> values = new ArrayList<>();
    values.addAll(data.keySet());
    str.put(AccumulatorUtils.addToMetricName(prefix, "values"),
            values.stream()
                    .sorted(Comparator.comparingDouble(a -> -data.get(a).getTotal()))
                    .map(a -> String.valueOf(a))
                    .collect(Collectors.joining(",")));

  }
}
