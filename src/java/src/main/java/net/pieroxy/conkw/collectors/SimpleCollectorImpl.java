package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.Collection;

public class SimpleCollectorImpl implements SimpleCollector {
    private ResponseData collectionInProgress;
    private ResponseData completedCollection;
    private String configKey;
    protected Grabber grabber;

    public SimpleCollectorImpl(Grabber g, String configKey) {
        this.grabber = g;
        this.completedCollection = new ResponseData(g, System.currentTimeMillis(), HashMapPool.SIZE_UNKNOWN, HashMapPool.SIZE_UNKNOWN);
        this.collectionInProgress = new ResponseData(g, System.currentTimeMillis(), HashMapPool.SIZE_UNKNOWN, HashMapPool.SIZE_UNKNOWN);
        this.configKey = configKey;
    }

    @Override
    public synchronized void collectionDone() {
        try (ResponseData tmp = completedCollection) {
            completedCollection = collectionInProgress;
            collectionInProgress = new ResponseData(grabber, System.currentTimeMillis(), tmp.getNum().size(), tmp.getStr().size());
        }
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

    public void setTime(long time) {
        collectionInProgress.setElapsedToGrab(time);
    }

    @Override
    public void copyDataFrom(ResponseData data) {
        try (ResponseData tmp = collectionInProgress) {
            collectionInProgress = new ResponseData(data);
        }
    }

    @Override
    public Collection<String> getErrors() {
        return completedCollection.getErrors();
    }

    @Override
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
        return new ResponseData(completedCollection);
    }

    @Override
    public void fillCollector(Collector toFill) {
        completedCollection.getNum().entrySet().forEach(entry -> toFill.collect(entry.getKey(), entry.getValue()));
        completedCollection.getStr().entrySet().forEach(entry -> toFill.collect(entry.getKey(), entry.getValue()));
        completedCollection.getErrors().forEach(error -> toFill.addError(error));
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
}
