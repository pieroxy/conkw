package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.utils.PrefixedKeyMap;
import org.junit.Test;

import java.util.Collection;

public abstract class AbstractAccumulatorTest<T extends Accumulator<Data>> extends ConkwTestCase {
    protected abstract T buildAccumulator();
    protected abstract Collection<Data> buildData();
    protected abstract void assertAccumulatorInternalState(T acc);
    protected abstract void assertAccumulatorLog(Data log);
    public abstract void test();

    @Test
    public void testInitializeFromData() {
        T accumulator1 = buildAccumulator();
        buildData().stream().forEach(accumulator1::add);
        accumulator1.prepareNewSession();
        assertAccumulatorInternalState(accumulator1);

        Data log = new Data();
        accumulator1.log("", log.getValues(), log.getDimensions());
        assertAccumulatorLog(log);
        T accumulator2 = buildAccumulator();
        accumulator2.initializeFromData(new PrefixedKeyMap<>(log.getValues()), new PrefixedKeyMap<>(log.getDimensions()));
        accumulator2.prepareNewSession();
        assertAccumulatorInternalState(accumulator2);
        Data log2 = new Data();
        accumulator2.log("", log2.getValues(), log2.getDimensions());
        log.assertEquals(log2, this);
        assertAccumulatorLog(log2);
    }
}
