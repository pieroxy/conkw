package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.AbstractHistogramAccumulator;
import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.pub.mdlog.DataRecord;

import java.util.ArrayList;
import java.util.List;

public class SimpleLogHistogram<T extends DataRecord> extends AbstractHistogramAccumulator<T> {
    public static final String NAME = "loghist";

    private final List<Double> thresholds;
    private String valueKey;
    private double base;
    private double max;

    public SimpleLogHistogram(String valueKey, double base, double max) {
        this.valueKey = valueKey;
        this.base = base;
        this.max = max;
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
    public Double getValue(DataRecord line) {
        return line.getValues().get(valueKey);
    }

    @Override
    public Accumulator<T> getFreshInstance() {
        return new SimpleLogHistogram<T>(valueKey, base, max);
    }
}
