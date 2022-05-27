package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.PrefixedKeyMap;

import java.util.List;
import java.util.Map;

public abstract class AbstractHistogramAccumulator<T extends DataRecord> implements Accumulator<T> {
    Data data;
    Data lastData;

    public abstract List<Double> getThresholds();
    public abstract Double getValue(T line);

    /**
     * Needs to be called before any operation and after getThresholds is set to return the thresholds.
     */
    protected void init() {
        data = new Data(getThresholds().size()+1);
        lastData = new Data(getThresholds().size()+1);
    }

    @Override
    public void sumWith(Accumulator acc) {
        throw new RuntimeException("Not Implemented");
    }

    @Override
    public double getTotal() {
        return lastData.globalSum;
    }

    @Override
    public double add(T line) {
        Double value = getValue(line);
        if (value == null) return -1;

        data.globalCount++;
        data.globalSum += value;
        int histoPos = findHistoPos(value);
        data.histogram[histoPos]++;
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
    public void prepareNewSession() {
        lastData.copyFrom(data);
        data.globalSum = data.globalCount = 0;
        for (int i=0 ; i<data.histogram.length ; i++) data.histogram[i]=0;
    }

    @Override
    public void log(String prefix, DataRecord record) {
        record.getValues().put(AccumulatorUtils.addToMetricName(prefix, "total"), lastData.globalSum);
        record.getValues().put(AccumulatorUtils.addToMetricName(prefix, "count"), lastData.globalCount);

        if (lastData.globalCount>0)
            record.getValues().put(AccumulatorUtils.addToMetricName(prefix, "avg"), lastData.globalSum/lastData.globalCount);

        List<Double> thresholds = getThresholds();
        StringBuilder dims = new StringBuilder();
        for (int i=0 ; i<lastData.histogram.length ; i++) {
            double current = lastData.histogram[i];
            if (i>0) dims.append(",");
            if (i==lastData.histogram.length-1) {
                record.getValues().put(AccumulatorUtils.addToMetricName(prefix, "above", "histValue"), current);
                dims.append(AccumulatorUtils.cleanMetricPathElement("above"));
            } else {
                record.getValues().put(AccumulatorUtils.addToMetricName(prefix, String.valueOf(thresholds.get(i)), "histValue"), current);
                dims.append(AccumulatorUtils.cleanMetricPathElement(String.valueOf(thresholds.get(i))));
            }
        }
        record.getDimensions().put(AccumulatorUtils.addToMetricName(prefix, "histValues"), dims.toString());
    }

    @Override
    public void initializeFromData(PrefixedKeyMap<Double> num, PrefixedKeyMap<String> str) {

    }
}

class Data {
    double globalSum;
    double globalCount;
    double[]histogram = null;

    void copyFrom(Data d) {
        if (d == null) {
            globalCount = globalSum = 0;
        } else {
            globalCount = d.globalCount;
            globalSum = d.globalSum;
            System.arraycopy(d.histogram, 0, histogram, 0, histogram.length);
        }
    }

    public Data(int size) {
        this.histogram = new double[size];
    }
}