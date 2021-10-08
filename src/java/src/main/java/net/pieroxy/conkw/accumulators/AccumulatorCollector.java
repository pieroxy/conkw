package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.accumulators.implementations.RootAccumulator;
import net.pieroxy.conkw.collectors.Collector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.ArrayList;
import java.util.Collection;

public class AccumulatorCollector<T extends LogRecord> implements Collector {
  final SimpleTransientCollector sc;

  private final RootAccumulator<T> accumulator;
  private final Grabber grabber;
  private String configKey;

  Collection<String> errors = new ArrayList<>();

  public AccumulatorCollector(Grabber g, String configKey, String metricName, Accumulator<T> accumulator) {
    grabber = g;
    this.configKey = configKey;
    this.accumulator = new RootAccumulator<T>(metricName, accumulator);
    this.sc = new SimpleTransientCollector(g, "");
  }

  @Override
  public ResponseData getDataCopy() {
    ResponseData res = new ResponseData(grabber, System.currentTimeMillis());
    accumulator.log("", res.getNum(), res.getStr());
    ResponseData rd = sc.getDataCopy();
    rd.getNum().entrySet().forEach(entry -> res.addMetric(entry.getKey(), entry.getValue()));
    rd.getStr().entrySet().forEach(entry -> res.addMetric(entry.getKey(), entry.getValue()));
    rd.getErrors().forEach(entry -> res.addError(entry));
    rd.setTimestamp(sc.getTimestamp());
    rd.setElapsedToGrab(accumulator.getLastPeriod());
    errors.forEach(res::addError);
    return res;
  }

  @Override
  public void fillCollector(Collector c) {
    sc.fillCollector(c);
  }

  @Override
  public Collection<String> getErrors() {
    return errors;
  }

  @Override
  public void collectionDone() {
    accumulator.prepareNewSession();
    sc.collectionDone();
  }

  @Override
  public void setTime(long time) {
    sc.setTime(time);
  }

  @Override
  public void setTimestamp(long time) {
    sc.setTimestamp(time);
  }

  @Override
  public long getTime() {
    return sc.getTime();
  }

  @Override
  public void addError(String message) {
    errors.add(message);
  }

  @Override
  public boolean hasError() {
    return errors.size()>0;
  }

  @Override
  public long getTimestamp() {
    return 0;
  }

  @Override
  public void collect(String metric, double value) {
    sc.collect(metric, value);
  }

  @Override
  public void collect(String metric, String value) {
    sc.collect(metric, value);
  }

  @Override
  public String getConfigKey() {
    return configKey;
  }

  @Override
  public void close() {
    // will close accumulators when they will be Closable
  }
}
