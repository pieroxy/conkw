package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.accumulators.implementations.RootAccumulator;
import net.pieroxy.conkw.collectors.Collector;
import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.ArrayList;
import java.util.Collection;

public class AccumulatorCollector<T extends LogRecord> implements Collector {

  private final RootAccumulator<T> accumulator;
  private final Grabber grabber;

  Collection<String> errors = new ArrayList<>();
  long time;

  public AccumulatorCollector(Grabber g, String metricName, Accumulator<T> accumulator) {
    grabber = g;
    this.accumulator = new RootAccumulator<T>(metricName, accumulator);
  }

  @Override
  public ResponseData getData() {
    ResponseData res = new ResponseData(grabber, System.currentTimeMillis());
    accumulator.log("", res.getNum(), res.getStr());
    errors.forEach(res::addError);
    return res;
  }

  @Override
  public void reset() {

  }

  @Override
  public void setTime(long time) {
    this.time = time;
  }

  @Override
  public long getTime() {
    return time;
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
}
