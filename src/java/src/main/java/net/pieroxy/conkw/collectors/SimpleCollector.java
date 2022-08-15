package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.accumulators.implementations.RootAccumulator;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.Collection;
import java.util.logging.Level;

import static net.pieroxy.conkw.utils.pools.hashmap.HashMapPool.SIZE_UNKNOWN;

public class SimpleCollector implements Collector {
    private ResponseData collectionInProgress;
    private ResponseData completedCollection;
    private RootAccumulator accumulator;
    private final String configKey;
    protected Grabber<?, ?> grabber;

    public SimpleCollector(Grabber<?, ?> g, String configKey, RootAccumulator accumulator) {
        this.grabber = g;
        this.completedCollection = new ResponseData(g, System.currentTimeMillis(), SIZE_UNKNOWN, SIZE_UNKNOWN);
        this.collectionInProgress = new ResponseData(g, System.currentTimeMillis(), SIZE_UNKNOWN, SIZE_UNKNOWN);
        this.configKey = configKey;
        this.accumulator = accumulator;
    }

    @Override
    public synchronized void collectionDone() {
        if (accumulator!=null) accumulator.prepareNewSession();
        try (ResponseData tmp = completedCollection) {
            if (tmp == null) {
                grabber.log(Level.WARNING, "completedCollection is null");
                completedCollection = collectionInProgress;
                collectionInProgress = new ResponseData(grabber, System.currentTimeMillis(), SIZE_UNKNOWN, SIZE_UNKNOWN);
            } else {
                if (tmp.isClosed()) grabber.log(Level.WARNING, "completedCollection was already closed");
                if (!tmp.isOpened()) grabber.log(Level.WARNING, "completedCollection was not opened");
                completedCollection = collectionInProgress;
                collectionInProgress = new ResponseData(grabber, System.currentTimeMillis(), tmp.getNumSize(), tmp.getStrSize());
            }
        }
    }

    public RootAccumulator getAccumulator() {
        return accumulator;
    }

    // Writing

    @Override
    public synchronized void collect(String metric, double value) {
        collectionInProgress.addMetric(metric, value);
    }

    @Override
    public synchronized void collect(String metric, String value) {
        collectionInProgress.addMetric(metric, value);
    }

    /**
     * Sets the time spend to collect this data, if not already set.
     * @param time The time taken to grab data in this collector.
     */
    public void setTime(long time) {
        if (collectionInProgress.getElapsedToGrab() == 0) {
            collectionInProgress.setElapsedToGrab(time);
        }
    }

    public void copyDataFrom(ResponseData data) {
        try (ResponseData tmp = collectionInProgress) {
            collectionInProgress = new ResponseData(data);
        }
    }

    @Override
    public Collection<String> getErrors() {
        return completedCollection.getErrors();
    }

    public void copyDataFrom(SimpleCollector data) {
        try (ResponseData tmp = collectionInProgress) {
            collectionInProgress = data.getDataCopy();
        }
    }

    @Override
    public void addError(String message) {
        collectionInProgress.addError(message);
    }

    @Override
    public void setTimestamp(long time) {
        collectionInProgress.setTimestamp(time);
    }

    // Reading from the stable version in memory

    @Override
    public boolean hasError() {
        return !completedCollection.getErrors().isEmpty();
    }

    @Override
    public long getTimestamp() {
        return completedCollection.getTimestamp();
    }

    @Override
    public long getTime() {
        return completedCollection.getElapsedToGrab();
    }

    @Override
    public String getConfigKey() {
        return configKey;
    }

    @Override
    public synchronized ResponseData getDataCopy() {
        ResponseData res = new ResponseData(completedCollection);
        if (accumulator!=null) {
            accumulator.log("", res);
            res.setTimestamp(Math.max(res.getTimestamp(), accumulator.getLastPoint()));
        }
        return res;
    }

    @Override
    public void fillCollector(Collector toFill) {
        completedCollection.getNum().forEach(toFill::collect);
        completedCollection.getStr().forEach(toFill::collect);
        completedCollection.getErrors().forEach(toFill::addError);
    }

    @Override
    public String toString() {
        return "AbstractSimpleCollector{" +
            "collectionInProgress=" + collectionInProgress +
            ", completedCollection=" + completedCollection +
            ", configKey='" + configKey + '\'' +
            ", grabber=" + grabber +
            '}';
    }

    @Override
    public void close() {
        collectionInProgress.close();
        completedCollection.close();
    }

    public void setInitialized(boolean initialized) {
        collectionInProgress.setInitialized(initialized);
    }
}
