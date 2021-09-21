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

  Collection<String> errors = new ArrayList<>();

  public AccumulatorCollector(Grabber g, String metricName, Accumulator<T> accumulator) {
    grabber = g;
    this.accumulator = new RootAccumulator<T>(metricName, accumulator);
    this.sc = new SimpleTransientCollector(g);
  }

  @Override
  public ResponseData getData() {
    ResponseData res = new ResponseData(grabber, System.currentTimeMillis());
    accumulator.log("", res.getNum(), res.getStr());
    ResponseData rd = sc.getData();
    rd.getNum().entrySet().forEach(entry -> res.addMetric(entry.getKey(), entry.getValue()));
    rd.getStr().entrySet().forEach(entry -> res.addMetric(entry.getKey(), entry.getValue()));
    rd.getErrors().forEach(entry -> res.addError(entry));
    rd.setTimestamp(sc.getTimestamp());
    rd.setElapsedToGrab(accumulator.getLastPeriod());
    errors.forEach(res::addError);
    return res;
  }

  @Override
  public void prepareForCollection() {
    accumulator.prepareNewSession();
    sc.prepareForCollection();
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
}
