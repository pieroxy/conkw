package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations;

import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.AccumulatorProvider;

public class StableKeyAccumulator extends StringKeyAccumulator {
    public static final String NAME = "stablekey";

    private final int maxBuckets;

    public StableKeyAccumulator(String dimensionName, AccumulatorProvider acc, double maxBuckets) {
        super(dimensionName, acc);
        this.maxBuckets = (int)maxBuckets;
    }

    @Override
    public Integer getMaxBuckets() {
        return maxBuckets;
    }
}
