package net.pieroxy.conkw.accumulators.implementations;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class SumAccumulatorTest extends AbstractAccumulatorTest<SumAccumulator<Data>> {

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

    @Override
    protected SumAccumulator<Data> buildAccumulator() {
        return new SumAccumulator<>("vv.v", -10);
    }

    @Override
    protected Collection<Data> buildData() {
        return Arrays.stream(new Data[] {
            new Data().addVal("vv.v", 12.),
            new Data().addVal("abc", 155.).addVal("vv.v", 7.),
            new Data().addVal("avvv", 7.),
        }).collect(Collectors.toList());
    }

    @Override
    protected void assertAccumulatorInternalState(SumAccumulator<Data> acc) {
        assertEquals(9., acc.getTotal());
    }

    @Override
    protected void assertAccumulatorLog(Data log) {
        assertMapContains(log.getValues(), "vv_dv.sum", 9.);
    }
}

