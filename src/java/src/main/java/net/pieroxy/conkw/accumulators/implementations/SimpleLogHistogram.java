package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.accumulators.AbstractHistogramAccumulator;
import net.pieroxy.conkw.accumulators.Accumulator;

import java.util.ArrayList;
import java.util.List;

public class SimpleLogHistogram extends AbstractHistogramAccumulator {
    public static final String NAME = "loghist";


    private final List<Double> thresholds;
    private String valueKey;
    private double base;

    public SimpleLogHistogram(String valueKey, double base, double max) {
        this.valueKey = valueKey;
        this.base = base;
        double t = 1;
        max = Math.abs(max);
        thresholds = new ArrayList<>();
        while (t < max) {
            thresholds.add(t);
            t*=base;
        }
        super.init();
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
}
