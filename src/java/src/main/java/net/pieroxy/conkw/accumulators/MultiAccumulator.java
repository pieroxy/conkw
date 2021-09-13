package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MultiAccumulator<T extends LogRecord> implements Accumulator<T> {
  public static final String NAME = "multi";

  private final List<Accumulator<T>> accumulators = new ArrayList<>();

  public MultiAccumulator(Accumulator[]accumulators) {
    for (Accumulator a : accumulators) {
      this.accumulators.add(a);
    }
  }

  public Accumulator<T> getAccumulator(int index) {
    return accumulators.get(index);
  }

  public int getLength() {
    return accumulators.size();
  }

  @Override
  public double add(T line) {
    double d = 0;
    for (Accumulator<T> acc : accumulators) d+=acc.add(line);
    return d;
  }

  @Override
  public void sumWith(Accumulator acc) {
    MultiAccumulator<T> ma = (MultiAccumulator<T>) acc;
    if (ma.getLength()!=getLength()) throw new ClassCastException("Trying to sum two different MultiAccumulators");
    for (int i=0 ; i<getLength() ; i++) {
      getAccumulator(i).sumWith(ma.getAccumulator(i));
    };
  }

  @Override
  public double getTotal() {
    double d = 0;
    for (Accumulator<T> acc : accumulators) d+=acc.getTotal();
    return d;
  }

  @Override
  public void reset() {
    for (Accumulator<T> acc : accumulators) acc.reset();
  }

  @Override
  public void log(String prefix, Map<String, Double> num, Map<String, String> str) {
    for (Accumulator<T> acc : accumulators) acc.log(prefix, num, str);
  }
}
