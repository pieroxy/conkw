package net.pieroxy.conkw.accumulators.implementations;

import java.util.Arrays;
import java.util.Collection;

public class SimpleLogHistogramTest extends AbstractAccumulatorTest<SimpleLogHistogram<Data>> {
    @Override
    protected SimpleLogHistogram buildAccumulator() {
        return new SimpleLogHistogram("time", 10, 1e10);
    }

    @Override
    protected Collection<Data> buildData() {
        return Arrays.asList(
                new Data().addVal("time", 1.2),
                new Data().addVal("time", 12.),
                new Data().addVal("time", 13.),
                new Data().addVal("time", 3E8),
                new Data().addVal("time", 130.),
                new Data().addVal("time", 13e100),
                new Data().addVal("time", 13.)
        );
    }

    @Override
    protected Collection<Data> buildExtraData() {
        return Arrays.asList(
                new Data().addVal("time", 1.23),
                new Data().addVal("time", 12.3),
                new Data().addVal("time", 13.3),
                new Data().addVal("time", 3.3E8),
                new Data().addVal("time", 130.3),
                new Data().addVal("time", 13.3e100),
                new Data().addVal("time", 13.3)
        );
    }

    @Override
    protected void assertAccumulatorLog(Data log) {
        assertMapContains(log.getValues(), "10.histValue", 1.);
        assertMapContains(log.getValues(), "100.histValue", 3.);
        assertMapContains(log.getValues(), "1000.histValue", 1.);
        assertMapContains(log.getValues(), "10000.histValue", 0.);
        assertMapContains(log.getValues(), "100000.histValue", 0.);
        assertMapContains(log.getValues(), "1000000.histValue", 0.);
        assertMapContains(log.getValues(), "1_d0E7.histValue", 0.);
        assertMapContains(log.getValues(), "1_d0E8.histValue", 0.);
        assertMapContains(log.getValues(), "1_d0E9.histValue", 1.);
        assertFalse(log.getValues().containsKey("1E10.histValue"));
        assertMapContains(log.getValues(), "above.histValue", 1.);
        assertMapContains(log.getValues(), "count", 7.);
        assertMapContains(log.getValues(), "total", 1.2+12.+13.+3E8+130.+13e100+13.);

    }

    @Override
    protected void assertAccumulatorInternalState(SimpleLogHistogram acc) {
    }
}
