package net.pieroxy.conkw.grabbersBase;

import net.pieroxy.conkw.utils.hashing.Md5Sum;

import java.util.List;

public class PartiallyExtractableConfig {
  private List<String> toExtract;

  public List<String> getToExtract() {
    return toExtract;
  }

  public void setToExtract(List<String> toExtract) {
    this.toExtract = toExtract;
  }

  public void addToHash(Md5Sum sum) {
    if (toExtract!=null) toExtract.forEach(sum::add);
  }
}
