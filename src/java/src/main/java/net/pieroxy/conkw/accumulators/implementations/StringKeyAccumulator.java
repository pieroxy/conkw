package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.pub.mdlog.LogRecord;
import net.pieroxy.conkw.accumulators.*;
import net.pieroxy.conkw.utils.PrefixedKeyMap;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StringKeyAccumulator<T extends LogRecord> extends KeyAccumulator<String, T> {
  private final static Logger LOGGER = Logger.getLogger(StringKeyAccumulator.class.getName());
  public static final String NAME = "stringkey";

  public interface StringProvider<T> {
    String getValue(T t);
  }

  private final String dimensionName;

  public StringKeyAccumulator(String dimensionName, AccumulatorProvider<T> ap) {
    super(ap);
    this.dimensionName = dimensionName;
  }

  @Override
  public String getKey(T line) {
    return AccumulatorUtils.cleanMetricPathElement(line.getDimensions().get(dimensionName));
  }

  @Override
  public void initializeFromData(PrefixedKeyMap<Double> num, PrefixedKeyMap<String> str) {
    String valuesAsString = str.get("values");
    if (valuesAsString == null) return;
    String[]values = valuesAsString.split(",");
    current.setTotal(num.get("total"));

    for (String k : values) {
      Accumulator<T> acc = current.getData().getOrDefault(k, null);
      if (acc==null) try {
        current.getData().put(k, acc = buildNewAccumulator());
        num.pushPrefix(k+".");
        str.pushPrefix(k+".");
        acc.initializeFromData(num,str);
        num.popPrefix();
        str.popPrefix();
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Cannot create accumulator", e);
      }
    }
  }

  public String getDimensionName() {
    return dimensionName;
  }
}
