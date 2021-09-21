package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.accumulators.AbstractHistogramAccumulator;
import net.pieroxy.conkw.accumulators.Accumulator;

import java.util.ArrayList;
import java.util.List;

public class Simple125Histogram extends AbstractHistogramAccumulator {
  public static final String NAME = "log125hist";
  private final String valueKey;
  private final ArrayList<Double> thresholds;

  public Simple125Histogram(String valueKey, double min, double max) {
    this.valueKey = valueKey;
    double scale = 1;
    int rot = 0;
    boolean inrange = false;
    thresholds = new ArrayList<>();
    while (true) {
      double b = getBaseNumber(rot) * scale;
      if (!inrange) {
        if (b >= min) {
          inrange=true;
        }
      }
      if (inrange) {
        if (b>max) break;
        thresholds.add(b);
      }
      rot++;
      if (rot>2) {
        rot = 0;
        scale*=10;
      }
    }
    super.init();
  }

  private double getBaseNumber(int rot) {
    switch (rot%3) {
      case 0 : return 1;
      case 1 : return 2;
      case 2 : return 5;
    }
    throw new RuntimeException("Should not happen");
  }

  @Override
  public List<Double> getThresholds() {
    return thresholds;
  }

  @Override
  public Double getValue(LogRecord line) {
    return line.getValues().get(valueKey);
  }

  public String getValueKey() {
    return valueKey;
  }
}
