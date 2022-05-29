package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

public class SumAccumulator<T extends DataRecord> implements Accumulator<T> {
  public static final String NAME = "sum";
  private final String valueName;
  private double defaultValue;
  private double value;
  private double lastValue = 0;

  public SumAccumulator(String valueName, double defaultValue) {
    this.valueName = valueName;
    this.defaultValue = defaultValue;
  }

  @Override
  public double add(T line) {
    Double lv = line.getValues().get(valueName);
    if (lv==null) lv = defaultValue;
    value += lv;
    return lv;
  }

  @Override
  public void sumWith(Accumulator acc) {
    value += ((SumAccumulator)acc).lastValue;
  }

  @Override
  public void initializeFromData(PrefixedDataRecord record) {
    Double value = record.getValues().get(AccumulatorUtils.cleanMetricPathElement(valueName) + "." + NAME);
    if (value==null) this.value = 0;
    else this.value = value;
  }

  @Override
  public double getTotal() {
    return lastValue;
  }

  @Override
  public void log(String prefix, DataRecord record) {
    record.getValues().put(AccumulatorUtils.addToMetricName(prefix, valueName, NAME), lastValue);
  }

  @Override
  public Accumulator<T> getFreshInstance() {
    return new SumAccumulator<T>(valueName, defaultValue);
  }

  @Override
  public void prepareNewSession() {
    lastValue = value;
    value=0;
  }


  public String getValueName() {
    return valueName;
  }

  public double getValue() {
    return value;
  }
}
