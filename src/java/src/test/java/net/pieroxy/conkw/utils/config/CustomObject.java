package net.pieroxy.conkw.utils.config;

import net.pieroxy.conkw.accumulators.implementations.RootAccumulator;
import net.pieroxy.conkw.utils.duration.CDuration;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

public class CustomObject {
  private Double doubleValue;
  private List<Double> doubleList;
  private CustomObject subObject;
  private List<CustomObject> customList;
  private CDuration duration;
  private Pattern pattern;
  private RootAccumulator accumulator;
  private URL url;

  public Double getDoubleValue() {
    return doubleValue;
  }

  public void setDoubleValue(Double doubleValue) {
    this.doubleValue = doubleValue;
  }

  public List<Double> getDoubleList() {
    return doubleList;
  }

  public void setDoubleList(List<Double> doubleList) {
    this.doubleList = doubleList;
  }

  public CustomObject getSubObject() {
    return subObject;
  }

  public void setSubObject(CustomObject subObject) {
    this.subObject = subObject;
  }

  public List<CustomObject> getCustomList() {
    return customList;
  }

  public void setCustomList(List<CustomObject> customList) {
    this.customList = customList;
  }

  public CDuration getDuration() {
    return duration;
  }

  public void setDuration(CDuration duration) {
    this.duration = duration;
  }

  public Pattern getPattern() {
    return pattern;
  }

  public void setPattern(Pattern pattern) {
    this.pattern = pattern;
  }

  public RootAccumulator getAccumulator() {
    return accumulator;
  }

  public void setAccumulator(RootAccumulator accumulator) {
    this.accumulator = accumulator;
  }

  public URL getUrl() {
    return url;
  }

  public void setUrl(URL url) {
    this.url = url;
  }
}
