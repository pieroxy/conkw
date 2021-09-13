package net.pieroxy.conkw.accumulators;

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
  Map<A, Double> floatingWeights = new HashMap<>();

  abstract public A getKey(T record);

  protected Integer getMaxBuckets() {
    return null;
  }

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
  public void sumWith(Accumulator acc) {
    KeyAccumulator<A, T> ka = (KeyAccumulator) acc;
    ka.data.entrySet().stream().forEach(entry -> {
      Accumulator a = data.get(entry.getKey());
      try {
        if (a == null) {
          a = provider.getAccumulator();
          data.put(entry.getKey(), a);
        }
        a.sumWith(entry.getValue());
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "", e);
      }
    });
  }

  @Override
  public void reset() {
    if (getMaxBuckets()!=null) {
      // Keep all the keys ever seen
      data.entrySet().forEach(e -> {
        Double d = floatingWeights.get(e.getKey());
        if (d==null) d = 0d;
        floatingWeights.put(e.getKey(), 0.9*d+e.getValue().getTotal());
        e.getValue().reset();
      });
    } else {
      data.clear();
    }
    total = 0;
  }

  @Override
  public void log(String prefix, Map<String, Double> num, Map<String, String> str) {
    if (getMaxBuckets()==null)
      logSimple(prefix, num, str);
    else
      logStable(prefix, num, str);
  }

  void logStable(String prefix, Map<String, Double> num, Map<String, String> str) {
    try {
      Set<Tuple> tree = new TreeSet<>();
      data.entrySet().forEach(e -> tree.add(new Tuple(String.valueOf(e.getKey()), floatingWeights.getOrDefault(e.getKey(), 0d) + e.getValue().getTotal(), e.getValue())));
      StringBuilder keys = new StringBuilder();
      int detail = getMaxBuckets();
      Accumulator others = null;
      for (Tuple t : tree) {
        if (detail-- > 0) {
          t.acc.log(AccumulatorUtils.addToMetricName(prefix, String.valueOf(t.key)), num, str);
          if (keys.length() > 0) keys.append(',');
          keys.append(t.key);
        } else {
          if (others == null) others = provider.getAccumulator();
          others.sumWith(t.acc);
        }
      }
      if (others!=null) {
        others.log(AccumulatorUtils.addToMetricName(prefix, "others"), num, str);
        if (keys.length() > 0) keys.append(',');
        keys.append("others");
      }
      num.put(AccumulatorUtils.addToMetricName(prefix, "total"), total);
      str.put(AccumulatorUtils.addToMetricName(prefix, "values"), keys.toString());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
  }

  public void logSimple(String prefix, Map<String, Double> num, Map<String, String> str) {
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

  static class Tuple implements Comparable {
    String key;
    double value;
    private Accumulator acc;

    public Tuple(String key, double value, Accumulator acc) {
      this.key = key;
      this.value = value;
      this.acc = acc;
    }

    @Override
    public int compareTo(Object o) {
      return -Double.compare(value, ((Tuple)o).value);
    }
  }
}
