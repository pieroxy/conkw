package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.utils.PrefixedKeyMap;

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

        Data log = new Data();
        sa.log("", log.getValues(), log.getDimensions());
        SumAccumulator sa2 = new SumAccumulator("vv.v", 0);
        sa2.initializeFromData(new PrefixedKeyMap<>(log.getValues()), new PrefixedKeyMap<>(log.getDimensions()));
        sa2.prepareNewSession();
        assertEquals(9., sa2.getTotal());
        Data log2 = new Data();
        sa2.log("", log2.getValues(), log2.getDimensions());
        log.assertEquals(log2, this);
    }
}

