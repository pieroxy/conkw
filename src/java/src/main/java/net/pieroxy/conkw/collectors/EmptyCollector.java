package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.ArrayList;
import java.util.Collection;

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
  public Collection<String> getErrors() {
    return new ArrayList<>();
  }

  @Override
  public void collectionDone() {
  }

  @Override
  public void setTime(long time) {
  }

  @Override
  public void setTimestamp(long time) {

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

  @Override
  public void collect(String metric, double value) {

  }

  @Override
  public void collect(String metric, String value) {

  }

  @Override
  public String getConfigKey() {
    return "";
  }
}
