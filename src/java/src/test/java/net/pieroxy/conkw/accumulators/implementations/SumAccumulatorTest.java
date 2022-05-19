package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.pub.mdlog.LogRecord;

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