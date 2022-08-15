package net.pieroxy.conkw.pub.mdlog;

import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.util.Map;

public class GenericLogRecord implements DataRecord {
    private Map<String, String> dims;
    private Map<String, Double> vals;

    public GenericLogRecord() {
        this(-1,-1);
    }

    public GenericLogRecord(int dimsSize, int valsSize) {
        dims = HashMapPool.getInstance().borrow(dimsSize);
        vals = HashMapPool.getInstance().borrow(valsSize);
    }

    @Override
    public Map<String, String> getDimensions() {
        return dims;
    }

    @Override
    public Map<String, Double> getValues() {
        return vals;
    }

    public GenericLogRecord addValue(String valName, double value) {
        vals.put(valName,value);
        return this;
    }

    public GenericLogRecord addDimension(String valName, String value) {
        dims.put(valName,value);
        return this;
    }

    @Override
    public void close() {
        HashMapPool.getInstance().giveBack(dims);
        HashMapPool.getInstance().giveBack(vals);
        dims=null;
        vals=null;
    }
}
