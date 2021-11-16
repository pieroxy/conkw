package net.pieroxy.conkw.utils.config;

import net.pieroxy.conkw.utils.duration.CDuration;

import java.util.List;

public class CustomObject {
  private Double doubleValue;
  private List<Double> doubleList;
  private CustomObject subObject;
  private List<CustomObject> customList;
  private CDuration duration;

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
}
