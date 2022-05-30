package net.pieroxy.conkw.accumulators.implementations;

import java.util.Arrays;
import java.util.Collection;

public class RootAccumulatorTest extends AbstractAccumulatorTest<RootAccumulator<Data>> {

    @Override
    protected RootAccumulator<Data> buildAccumulator() {
        return new RootAccumulator<>("toto", new SumAccumulator<>("vv.v", -10));
    }

    @Override
    protected Collection<Data> buildData() {
        return Arrays.asList(
                new Data().addVal("vv.v", 12.),
                new Data().addVal("abc", 155.).addVal("vv.v", 7.),
                new Data().addVal("avvv", 7.));
    }

    @Override
    protected Collection<Data> buildExtraData() {
        return Arrays.asList(
                new Data().addVal("vv.v", 0.1),
                new Data().addVal("abc", 155.).addVal("vv.v", 0.2),
                new Data().addVal("avvv", 7.));
    }

    @Override
    protected void assertAccumulatorInternalState(RootAccumulator<Data> acc) {
        assertEquals(9., acc.getTotal());
    }

    @Override
    protected void assertAccumulatorLog(Data log) {
        assertMapContains(log.getValues(), "vv_dv.sum", 9.);
    }

    @Override
    public void cleanupLoggedDataBeforeComparison(Data log) {
        super.cleanupLoggedDataBeforeComparison(log);
        log.getValues().remove(RootAccumulator.ELAPSED);
    }
}
