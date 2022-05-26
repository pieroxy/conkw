package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.MultiAccumulator;

import java.util.Arrays;
import java.util.Collection;

public class MultiAccumulatorTest extends AbstractAccumulatorTest<MultiAccumulator<Data>> {
    @Override
    protected MultiAccumulator buildAccumulator() {
        return new MultiAccumulator(new Accumulator[] {
                new NamedAccumulator("sum0", new SumAccumulator("tosum", 0)),
                new NamedAccumulator("sum10", new SumAccumulator("tosum", -10)),
                new SimpleCounter()
        });
    }

    @Override
    protected Collection<Data> buildData() {
        return Arrays.asList(new Data[]{
                new Data().addVal("tosum", 1.),
                new Data().addVal("nottosum", 10.),
                new Data().addVal("tosum", 1.),
                new Data().addVal("tosum", 1.),
                new Data().addVal("tosum", 1.),
        });
    }

    @Override
    protected void assertAccumulatorInternalState(MultiAccumulator acc) {
    }

    @Override
    protected void assertAccumulatorLog(Data log) {
        assertMapContains(log.getValues(), "count", 5.);
        assertMapContains(log.getValues(), "sum10.tosum.sum", -6.);
        assertMapContains(log.getValues(), "sum0.tosum.sum", 4.);
    }

    @Override
    public void test() {
    }
}