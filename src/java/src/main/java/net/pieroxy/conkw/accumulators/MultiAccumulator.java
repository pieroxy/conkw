package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiAccumulator<T extends DataRecord> implements Accumulator<T> {
  public static final String NAME = "multi";

  private final List<Accumulator<T>> accumulators = new ArrayList<>();

  public MultiAccumulator(Accumulator<T>[]accumulators) {
    this.accumulators.addAll(Arrays.asList(accumulators));
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
  public void sumWith(Accumulator<T> acc) {
    MultiAccumulator<T> ma = (MultiAccumulator<T>) acc;
    if (ma.getLength()!=getLength()) throw new ClassCastException("Trying to sum two different MultiAccumulators");
    for (int i=0 ; i<getLength() ; i++) {
      getAccumulator(i).sumWith(ma.getAccumulator(i));
    };
  }

  @Override
  public void initializeFromData(PrefixedDataRecord record) {
    accumulators.forEach(a -> a.initializeFromData(record));
  }

  @Override
  public double getTotal() {
    double d = 0;
    for (Accumulator<T> acc : accumulators) d+=acc.getTotal();
    return d;
  }

  @Override
  public void prepareNewSession() {
    for (Accumulator<T> acc : accumulators) acc.prepareNewSession();
  }

  @Override
  public void log(String prefix, DataRecord record) {
    for (Accumulator<T> acc : accumulators) acc.log(prefix, record);
  }

  @Override
  public Accumulator<T> getFreshInstance() {
    return new MultiAccumulator<>(accumulators.stream().map(Accumulator::getFreshInstance).toArray(Accumulator[]::new));
  }
}
