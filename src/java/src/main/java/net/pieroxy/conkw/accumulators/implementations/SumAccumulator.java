package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.pub.mdlog.LogRecord;
import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.utils.PrefixedKeyMap;

import java.util.Map;

public class SumAccumulator<T extends LogRecord> implements Accumulator<T> {
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
    lastValue += ((SumAccumulator)acc).lastValue;
  }

  @Override
  public void initializeFromData(PrefixedKeyMap<Double> num, PrefixedKeyMap<String> str) {

  }

  @Override
  public double getTotal() {
    return lastValue;
  }

  @Override
  public void log(String prefix, Map<String, Double> data, Map<String, String> str) {
    data.put(AccumulatorUtils.addToMetricName(prefix, valueName, NAME), lastValue);
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
