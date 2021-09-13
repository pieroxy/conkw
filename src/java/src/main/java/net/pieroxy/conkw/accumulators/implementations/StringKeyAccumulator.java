package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.accumulators.AccumulatorProvider;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.accumulators.KeyAccumulator;

public class StringKeyAccumulator<T extends LogRecord> extends KeyAccumulator<String, T> {
  public static final String NAME = "stringkey";

  public interface StringProvider<T> {
    String getValue(T t);
  }

  private final String dimensionName;
  private final AccumulatorProvider<T> ap;

  public StringKeyAccumulator(String dimensionName, AccumulatorProvider<T> ap) {
    super(ap);
    this.dimensionName = dimensionName;
    this.ap = ap;
  }

  @Override
  public String getKey(T line) {
    return AccumulatorUtils.cleanMetricPathElement(line.getDimensions().get(dimensionName));
  }

  public String getDimensionName() {
    return dimensionName;
  }

  public AccumulatorProvider<T> getAp() {
    return ap;
  }
}
