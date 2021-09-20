package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

public abstract class AbstractSimpleCollector implements SimpleCollector {
    protected ResponseData collected;
    protected Grabber grabber;

    public AbstractSimpleCollector(Grabber g) {
        this.grabber = g;
        this.collected = new ResponseData(g, System.currentTimeMillis());
    }

    @Override
    public synchronized void collect(String metric, double value) {
        collected.addMetric(metric, value);
    }

    @Override
    public synchronized void collect(String metric, String value) {
        collected.addMetric(metric, value);
    }

    @Override
    public synchronized ResponseData getData() {
        return collected;
    }

    public void setTime(long time) {
        collected.setElapsedToGrab(time);
    }

    @Override
    public long getTime() {
        return collected.getElapsedToGrab();
    }

    @Override
    public void setData(ResponseData data) {
        collected = data;
    }

    @Override
    public void setData(SimpleCollector data) {
        collected = data.getData();
    }

    @Override
    public void addError(String message) {
        collected.addError(message);
    }

    @Override
    public boolean hasError() {
        return !collected.getErrors().isEmpty();
    }

    @Override
    public long getTimestamp() {
        return collected.getTimestamp();
    }

}
