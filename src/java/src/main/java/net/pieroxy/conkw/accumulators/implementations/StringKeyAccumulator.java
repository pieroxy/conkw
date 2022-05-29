package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorProvider;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.accumulators.KeyAccumulator;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StringKeyAccumulator<T extends DataRecord> extends KeyAccumulator<String, T> {
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
  public void initializeFromData(PrefixedDataRecord record) {
    String valuesAsString = record.getDimensions().get("values");
    if (valuesAsString == null) return;
    String[]values = valuesAsString.split(",");
    current.setTotal(record.getValues().get("total"));

    for (String k : values) {
      Accumulator<T> acc = current.getData().getOrDefault(k, null);
      if (acc==null) try {
        current.getData().put(k, acc = getAccumulator());
        record.pushPrefix(k+".");
        acc.initializeFromData(record);
        record.popPrefix();
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Cannot create accumulator", e);
      }
    }
  }

  @Override
  public Accumulator<T> getFreshInstance() {
    return new StringKeyAccumulator<T>(dimensionName, this);
  }

  public String getDimensionName() {
    return dimensionName;
  }
}
