package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecord;

import java.util.List;

public abstract class AbstractHistogramAccumulator<T extends DataRecord> implements Accumulator<T> {
    HistogramData data;
    HistogramData lastData;

    public abstract List<Double> getThresholds();
    public abstract Double getValue(T line);

    /**
     * Needs to be called before any operation and after getThresholds is set to return the thresholds.
     */
    protected void init() {
        data = new HistogramData(getThresholds().size()+1);
        lastData = new HistogramData(getThresholds().size()+1);
    }

    @Override
    public void sumWith(Accumulator acc) {
        AbstractHistogramAccumulator other = (AbstractHistogramAccumulator) acc;
        data.globalSum += other.lastData.globalSum;
        data.globalCount += other.lastData.globalCount;
        for (int i=0 ; i<data.histogram.length ; i++) {
            data.histogram[i] += other.lastData.histogram[i];
        }
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

        StringBuilder dims = new StringBuilder();
        for (int i=0 ; i<lastData.histogram.length ; i++) {
            double current = lastData.histogram[i];
            if (i>0) dims.append(",");
            if (i==lastData.histogram.length-1) {
                record.getValues().put(AccumulatorUtils.addToMetricName(prefix, "above", "histValue"), current);
                dims.append(AccumulatorUtils.cleanMetricPathElement("above"));
            } else {
                String key = getKeyForSerialization(i);
                record.getValues().put(AccumulatorUtils.addToMetricName(prefix, key, "histValue"), current);
                dims.append(AccumulatorUtils.cleanMetricPathElement(key));
            }
        }
        record.getDimensions().put(AccumulatorUtils.addToMetricName(prefix, "histValues"), dims.toString());
    }

    String getKeyForSerialization(int index) {
        String key = String.valueOf(getThresholds().get(index));
        if (key.endsWith(".0")) key = key.substring(0, key.length()-2);
        return key;
    }

    @Override
    public void initializeFromData(PrefixedDataRecord r) {
        data.globalCount = r.getValues().get("count");
        data.globalSum = r.getValues().get("total");
        List<Double> thresholds = getThresholds();
        for (int i=0 ; i< thresholds.size() ; i++) {
            String key = AccumulatorUtils.cleanMetricPathElement(getKeyForSerialization(i));
            Double d = r.getValues().get(key + ".histValue");
            data.histogram[i] = d;
        }
        data.histogram[thresholds.size()] = r.getValues().get("above.histValue");
    }


    class HistogramData {
        double globalSum;
        double globalCount;
        double[]histogram = null;

        void copyFrom(HistogramData d) {
            if (d == null) {
                globalCount = globalSum = 0;
            } else {
                globalCount = d.globalCount;
                globalSum = d.globalSum;
                System.arraycopy(d.histogram, 0, histogram, 0, histogram.length);
            }
        }

        public HistogramData(int size) {
            this.histogram = new double[size];
        }
    }
}

