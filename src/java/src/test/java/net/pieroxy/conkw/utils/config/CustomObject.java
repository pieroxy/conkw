package net.pieroxy.conkw.utils.config;

import java.util.List;

public class CustomObject {
  private Double doubleValue;
  private List<Double> doubleList;
  private CustomObject subObject;
  private List<CustomObject> customList;

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
}
