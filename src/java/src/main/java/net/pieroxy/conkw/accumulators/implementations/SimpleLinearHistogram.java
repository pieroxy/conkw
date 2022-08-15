package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.AbstractHistogramAccumulator;
import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.pub.mdlog.DataRecord;

import java.util.ArrayList;
import java.util.List;

public class SimpleLinearHistogram<T extends DataRecord> extends AbstractHistogramAccumulator<T> {
    public static final String NAME = "linearhist";

    private final List<Double> thresholds;
    private String valueKey;
    private double min,max;
    private int steps;

    public SimpleLinearHistogram(String valueKey, double min, double max, double steps) {
        this.valueKey = valueKey;
        this.max = max;
        this.min = min;
        this.steps = (int)Math.round(steps);
        if (steps<2) throw new RuntimeException("Illegal argument. Steps should be at least 2.");
        double t = min;
        thresholds = new ArrayList<>(this.steps);
        while (t < max) {
            thresholds.add(t);
            t+=((max-min)/steps);
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
        return new SimpleLinearHistogram<>(valueKey, min, max, steps);
    }
}
