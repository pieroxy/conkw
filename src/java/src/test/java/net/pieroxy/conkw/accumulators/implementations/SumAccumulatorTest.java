package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.pub.mdlog.LogRecord;
import net.pieroxy.conkw.utils.PrefixedKeyMap;

import java.util.HashMap;
import java.util.Map;

public class SumAccumulatorTest extends ConkwTestCase {
    public void testSimple() {
        SumAccumulator<Data> sa = new SumAccumulator<>("vvv", 0);
        sa.add(new Data().addVal("vvv", 12.));
        sa.add(new Data().addVal("abc", 155.).addVal("vvv", 7.));
        sa.add(new Data().addVal("avvv", 7.));
        sa.prepareNewSession();
        assertEquals(19., sa.getTotal());
    }
    public void testDefaultValue() {
        SumAccumulator<Data> sa = new SumAccumulator<>("vvv", -10);
        sa.add(new Data().addVal("vvv", 12.));
        sa.add(new Data().addVal("abc", 155.).addVal("vvv", 7.));
        sa.add(new Data().addVal("avvv", 7.));
        sa.prepareNewSession();
        assertEquals(9., sa.getTotal());
    }
    public void testInitializeFromData() {
        SumAccumulator<Data> sa = new SumAccumulator<>("vv.v", -10);
        sa.add(new Data().addVal("vv.v", 12.));
        sa.add(new Data().addVal("abc", 155.).addVal("vv.v", 7.));
        sa.add(new Data().addVal("avvv", 7.));
        sa.prepareNewSession();

        Map<String,String> dims = new HashMap<>();
        Map<String,Double> vals = new HashMap<>();
        sa.log("", vals, dims);
        SumAccumulator sa2 = new SumAccumulator("vv.v", 0);
        sa2.initializeFromData(new PrefixedKeyMap<>(vals), new PrefixedKeyMap<>(dims));
        sa2.prepareNewSession();
        assertEquals(9., sa2.getTotal());
    }
}

class Data implements LogRecord {
    HashMap<String, String> dims = new HashMap<>();
    HashMap<String, Double> vals = new HashMap<>();

    public Data addDim(String name, String value) {
        dims.put(name, value);
        return this;
    }

    public Data addVal(String name, Double value) {
        vals.put(name, value);
        return this;
    }

    @Override
    public Map<String, String> getDimensions() {
        return dims;
    }

    @Override
    public Map<String, Double> getValues() {
        return vals;
    }

    @Override
    public void close() {
    }
}