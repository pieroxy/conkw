package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.io.PrintStream;
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
  public void add(T line) {
    for (Accumulator<T> acc : accumulators) acc.add(line);
  }

  @Override
  public void reset() {
    for (Accumulator<T> acc : accumulators) acc.reset();
  }

  @Override
  public void log(String prefix, Map<String, Double> num) {
    for (Accumulator<T> acc : accumulators) acc.log(prefix, num);
  }
}
