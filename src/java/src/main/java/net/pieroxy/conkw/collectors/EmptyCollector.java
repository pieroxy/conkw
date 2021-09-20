package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

public class EmptyCollector implements Collector {
  private Grabber grabber;

  public EmptyCollector(Grabber grabber) {
    this.grabber = grabber;
  }

  @Override
  public ResponseData getData() {
    return new ResponseData(grabber, System.currentTimeMillis());
  }

  @Override
  public void reset() {
  }

  @Override
  public void setTime(long time) {
  }

  @Override
  public long getTime() {
    return 0;
  }

  @Override
  public void addError(String message) {
  }

  @Override
  public boolean hasError() {
    return false;
  }

  @Override
  public long getTimestamp() {
    return 0;
  }
}
