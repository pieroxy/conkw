package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecordImpl;

import java.util.Arrays;
import java.util.Collection;

public class StringKeyAccumulatorTest extends AbstractAccumulatorTest<StringKeyAccumulator<Data>> {
    @Override
    protected StringKeyAccumulator<Data> buildAccumulator() {
        return new StringKeyAccumulator<>("dim", () -> new SumAccumulator<>("val", 0));
    }

    @Override
    protected Collection<Data> buildData() {
        return Arrays.asList(
                new Data().addDim("dim", "a").addVal("val", 12.),
                new Data().addDim("dim", "a").addVal("val", 11.),
                new Data().addDim("dim", "b").addVal("val", 10.),
                new Data().addDim("dim", "c").addVal("val", 2.));
    }

    @Override
    protected Collection<Data> buildExtraData() {
        return Arrays.asList(
                new Data().addDim("dim", "a").addVal("val", 12.1),
                new Data().addDim("dim", "a").addVal("val", 11.1),
                new Data().addDim("dim", "b").addVal("val", 10.1),
                new Data().addDim("dim", "abc").addVal("val", 10.11),
                new Data().addDim("dim", "c").addVal("val", 2.1));
    }

    @Override
    protected void assertAccumulatorInternalState(StringKeyAccumulator<Data> acc) {
    }

    @Override
    protected void assertAccumulatorLog(Data log) {
        assertMapContains(log.getValues(), "total", 35.);
        assertMapContains(log.getValues(), "a.val.sum", 23.);
        assertMapContains(log.getValues(), "b.val.sum", 10.);
        assertMapContains(log.getValues(), "c.val.sum", 2.);
    }

    public void test() {
        // This should not throw an exception
        buildAccumulator().initializeFromData(new PrefixedDataRecordImpl());
    }
}
