package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.Accumulator;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.AccumulatorUtils;

import java.io.PrintStream;
import java.util.Map;

public class SumAccumulator<T extends LogRecord> implements Accumulator<T> {
  public static final String NAME = "sum";
  private final String valueName;
  private double defaultValue;
  private double value;

  public SumAccumulator(String valueName, double defaultValue) {
    this.valueName = valueName;
    this.defaultValue = defaultValue;
  }

  @Override
  public void add(T line) {
    Double lv = line.getValues().get(valueName);
    if (lv!=null) value += lv;
    else value += defaultValue;
  }

  @Override
  public void log(String prefix, Map<String, Double> data) {
    data.put(AccumulatorUtils.addToMetricName(prefix, valueName, NAME), value);
  }

  @Override
  public void reset() {
    value=0;
  }


  public String getValueName() {
    return valueName;
  }

  public double getValue() {
    return value;
  }
}
