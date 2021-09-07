package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

import java.util.List;
import java.util.Map;

public abstract class AbstractHistogramAccumulator<T extends LogRecord> implements Accumulator<T> {
    private double globalSum;
    private double globalCount;
    private double[]histogram = null;

    public abstract List<Double> getThresholds();
    public abstract Double getValue(T line);

    @Override
    public double getTotal() {
        return globalSum;
    }

    @Override
    public double add(T line) {
        if (histogram == null) histogram = new double[getThresholds().size()+1];
        double value = getValue(line);

        globalCount++;
        globalSum += value;
        int histoPos = findHistoPos(value);
        histogram[histoPos]++;
        return value;
    }

    protected int findHistoPos(double value) {
        List<Double>t = getThresholds();

        if (value <= t.get(0)) return 0;
        for (int i=1 ; i<t.size() ; i++) {
            if (value <= t.get(i)) return i;
        }
        return t.size();
    }

    public static int log(int x, int b)
    {
        return (int) (Math.log(x) / Math.log(b));
    }


    @Override
    public void reset() {
        globalSum = globalCount = 0;
        for (int i=0 ; i<histogram.length ; i++) histogram[i]=0;
    }

    @Override
    public void log(String prefix, Map<String, Double> num, Map<String, String> str) {
        num.put(AccumulatorUtils.addToMetricName(prefix, "total"), globalSum);
        num.put(AccumulatorUtils.addToMetricName(prefix, "count"), globalCount);

        if (globalCount>0)
            num.put(AccumulatorUtils.addToMetricName(prefix, "avg"), globalSum/globalCount);

        List<Double> thresholds = getThresholds();
        for (int i=0 ; i<histogram.length ; i++) {
            double current = histogram[i];
            if (current!=0) {
                if (i==histogram.length-1)
                    num.put(AccumulatorUtils.addToMetricName(prefix, "above", "histValue"), current);
                else
                    num.put(AccumulatorUtils.addToMetricName(prefix, String.valueOf(thresholds.get(i)), "histValue"), current);
            }
        }
    }
}
