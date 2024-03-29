package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This abstract class will allow aggregations around a key and the number of occurrence of said key.
 * @param <A> The type of the key being considered.
 */
public abstract class KeyAccumulator<A, T extends DataRecord> implements Accumulator<T>, AccumulatorProvider<T> {
  private final static Logger LOGGER = Logger.getLogger(KeyAccumulator.class.getName());
  private final AccumulatorProvider<T> __provider;

  protected KeyAccumulatorData<A,T> current;
  KeyAccumulatorData<A,T> old;
  Map<A, Double> floatingWeights = HashMapPool.getInstance().borrow(HashMapPool.SIZE_UNKNOWN); // No possibility to predict

  abstract public A getKey(T record);

  protected Integer getMaxBuckets() {
    return null;
  }

  public KeyAccumulator(AccumulatorProvider<T> provider) {
    this.__provider = provider;
    current = new KeyAccumulatorData<>(HashMapPool.SIZE_UNKNOWN);
    old = new KeyAccumulatorData<>(HashMapPool.SIZE_UNKNOWN);
  }

  @Override
  public double getTotal() {
    return old.total;
  }

  @Override
  public Accumulator<T> getAccumulator() throws Exception {
    return __provider.getAccumulator();
  }

  @Override
  public double add(T line) {
    A key = getKey(line);
    if (key == null) return 0;

    Accumulator<T> acc = current.getData().getOrDefault(key, null);
    if (acc==null) try {
      current.getData().put(key, acc = getAccumulator());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Cannot create accumulator", e);
    }
    double value = acc.add(line);
    current.total += value;
    return value;
  }

  @Override
  public void sumWith(Accumulator<T> otherGeneric) {
    KeyAccumulator<A, T> other  = (KeyAccumulator<A, T>) otherGeneric;
    other.old.getData().forEach((key, value) -> {
      Accumulator<T> a = current.getData().get(key);
      try {
        if (a == null) {
          a = getAccumulator();
          current.getData().put(key, a);
        }
        a.sumWith(value);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "", e);
      }
    });
    current.total += other.old.total;
  }

  public void sum(KeyAccumulatorData<A, T> mine, KeyAccumulatorData<A, T> other) {
  }

  @Override
  public void prepareNewSession() {
    old.replaceData(current.getData());
    old.total = current.total;

    if (getMaxBuckets()!=null) {
      // Keep all the keys ever seen
      current.getData().forEach((key, value) -> {
        Double d = floatingWeights.get(key);
        if (d == null) d = 0d;
        floatingWeights.put(key, 0.9 * d + value.getTotal());
        value.prepareNewSession();
      });
    } else {
      current.getData().forEach((key, value) -> value.prepareNewSession());
      current.getData().clear();
    }
    current.total = 0;
  }

  @Override
  public void log(String prefix, DataRecord record) {
    if (getMaxBuckets()==null)
      logSimple(prefix, record);
    else
      logStable(prefix, record);
  }

  void logStable(String prefix, DataRecord record) {
    try {
      Set<Tuple<T>> tree = new TreeSet<>();
      old.getData().forEach((key, value) -> tree.add(new Tuple<T>(String.valueOf(key), floatingWeights.getOrDefault(key, 0d) + value.getTotal(), value)));
      StringBuilder keys = new StringBuilder();
      int detail = getMaxBuckets();
      Accumulator<T> others = null;
      for (Tuple<T> t : tree) {
        if (detail-- > 0) {
          t.acc.log(AccumulatorUtils.addToMetricName(prefix, String.valueOf(t.key)), record);
          if (keys.length() > 0) keys.append(',');
          keys.append(t.key);
        } else {
          if (others == null) others = getAccumulator();
          others.sumWith(t.acc);
        }
      }
      if (others!=null) {
        others.prepareNewSession();
        others.log(AccumulatorUtils.addToMetricName(prefix, "others"), record);
        if (keys.length() > 0) keys.append(',');
        keys.append("others");
      }
      record.getValues().put(AccumulatorUtils.addToMetricName(prefix, "total"), old.total);
      record.getDimensions().put(AccumulatorUtils.addToMetricName(prefix, "values"), keys.toString());
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "", e);
    }
  }

  public void logSimple(String prefix, DataRecord record) {
    for (Map.Entry<A, Accumulator<T>> entry : old.getData().entrySet()) {
      entry.getValue().log(AccumulatorUtils.addToMetricName(prefix, String.valueOf(entry.getKey())), record);
    }
    record.getValues().put(AccumulatorUtils.addToMetricName(prefix, "total"), old.total);
    List<A> values = new ArrayList<>();
    values.addAll(old.getData().keySet());
    record.getDimensions().put(AccumulatorUtils.addToMetricName(prefix, "values"),
            values.stream()
                    .sorted(Comparator.comparingDouble(a -> -old.getData().get(a).getTotal()))
                    .map(a -> String.valueOf(a))
                    .collect(Collectors.joining(",")));
  }

  static class Tuple<T extends DataRecord> implements Comparable {
    String key;
    double value;
    private Accumulator<T> acc;

    public Tuple(String key, double value, Accumulator<T> acc) {
      this.key = key;
      this.value = value;
      this.acc = acc;
    }

    @Override
    public int compareTo(Object o) {
      Tuple ot = (Tuple)o;
      int res = -Double.compare(value, ot.value);
      if (res == 0) res = key.compareTo(ot.key);
      return res;
    }
  }

  protected class KeyAccumulatorData<A, T extends DataRecord> implements Closeable {
    public KeyAccumulatorData(int size) {
      this.data = HashMapPool.getInstance().borrow(size);
    }

    double total;
    private Map<A, Accumulator<T>> data;

    @Override
    public void close() throws IOException {
      Map m = data;
      data = null;
      HashMapPool.getInstance().giveBack(m);
    }

    public Map<A, Accumulator<T>> getData() {
      return data;
    }

    public void replaceData(Map<A, Accumulator<T>> data) {
      Map old = this.data;
      this.data = HashMapPool.getInstance().borrow(data, (old.size()*100)/90); // Trying to account for more data.
      HashMapPool.getInstance().giveBack(old);
    }

    public void setTotal(double total) {
      this.total = total;
    }
  }
}


