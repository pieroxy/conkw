package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.AbstractHistogramAccumulator;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.Accumulator;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class SimpleLog10Histogram extends AbstractHistogramAccumulator {
    public static final String NAME = "log10hist";


    private final List<Double> thresholds;
    private String valueKey;

    public SimpleLog10Histogram(String valueKey, double max) {
        this.valueKey = valueKey;
        double t = 1;
        max = Math.abs(max);
        thresholds = new ArrayList<>();
        while (t < max) {
            thresholds.add(t);
            t*=10;
        }
    }

    @Override
    public List<Double> getThresholds() {
        return thresholds;
    }

    public String getValueKey() {
        return valueKey;
    }

    @Override
    public Double getValue(LogRecord line) {
        return line.getValues().get(valueKey);
    }

    @Override
    public void sumWith(Accumulator acc) {
        throw new NotImplementedException();
    }
}
