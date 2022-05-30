package net.pieroxy.conkw.utils.prefixeddata;

import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;

import java.util.Map;
import java.util.Objects;

public class PrefixedDataRecordImpl implements PrefixedDataRecord {
    private PrefixedKeyMap<String> dimensions;
    private PrefixedKeyMap<Double> values;

    public PrefixedDataRecordImpl(DataRecord log) {
        dimensions = new PrefixedKeyMap<>(log.getDimensions());
        values = new PrefixedKeyMap<>(log.getValues());
    }

    public PrefixedDataRecordImpl() {
        dimensions = new PrefixedKeyMap<>(HashMapPool.getInstance().borrow(HashMapPool.SIZE_UNKNOWN));
        values = new PrefixedKeyMap<>(HashMapPool.getInstance().borrow(HashMapPool.SIZE_UNKNOWN));
    }

    @Override
    public Map<String, String> getDimensions() {
        return dimensions;
    }

    @Override
    public Map<String, Double> getValues() {
        return values;
    }

    @Override
    public void close() {
        dimensions.close();
        values.close();
    }

    @Override
    public void pushPrefix(String prefix) {
        dimensions.pushPrefix(prefix);
        values.pushPrefix(prefix);
    }

    @Override
    public String popPrefix() {
        String a = dimensions.popPrefix();
        String b = values.popPrefix();
        if (!Objects.equals(a,b)) throw new RuntimeException("Prefixes mismatched: " + a + " vs " + b);
        return a;
    }

    @Override
    public String toString() {
        return "PrefixedDataRecordImpl{" +
                "dimensions=" + dimensions +
                ", values=" + values +
                '}';
    }
}
