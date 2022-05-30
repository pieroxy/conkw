package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecordImpl;
import org.junit.Test;

import java.util.Collection;

public abstract class AbstractAccumulatorTest<T extends Accumulator<Data>> extends ConkwTestCase {
    protected abstract T buildAccumulator();
    protected abstract Collection<Data> buildData();
    protected abstract Collection<Data> buildExtraData();
    protected abstract void assertAccumulatorInternalState(T acc);
    protected abstract void assertAccumulatorLog(Data log);

    @Test
    public final void testInitializeFromData() {
        T accumulator1 = buildAccumulator();
        buildData().stream().forEach(accumulator1::add);
        accumulator1.prepareNewSession();
        assertAccumulatorInternalState(accumulator1);

        Data log = new Data();
        accumulator1.log("", log);
        assertAccumulatorLog(log);
        T accumulator2 = buildAccumulator();
        accumulator2.initializeFromData(new PrefixedDataRecordImpl(log));
        accumulator2.prepareNewSession();
        assertAccumulatorInternalState(accumulator2);
        Data log2 = new Data();
        accumulator2.log("", log2);
        log.assertEquals(log2, this);
        assertAccumulatorLog(log2);
    }

    @Test
    public final void testSumWith() {
        T accumulator1 = buildAccumulator();
        T accumulator2 = buildAccumulator();
        T accumulator3 = buildAccumulator();
        buildData().stream().forEach(accumulator1::add);
        buildData().stream().forEach(accumulator3::add);
        buildExtraData().stream().forEach(accumulator2::add);
        buildExtraData().stream().forEach(accumulator3::add);
        accumulator1.prepareNewSession();
        accumulator2.sumWith(accumulator1);
        accumulator2.prepareNewSession();
        accumulator3.prepareNewSession();

        Data logA = new Data();
        accumulator2.log("", logA);
        Data logB = new Data();
        accumulator3.log("", logB);

        logA.assertEquals(logB, this);
    }

    @Test
    public final void testEmptyInitialization() {
        Data log = new Data();
        T accumulator1 = buildAccumulator();
        T accumulator2 = buildAccumulator();
        accumulator1.initializeFromData(new PrefixedDataRecordImpl(log));

        Data logA = new Data();
        accumulator1.log("", logA);
        Data logB = new Data();
        accumulator2.log("", logB);
        logA.assertEquals(logB, this);

    }
}
