package net.pieroxy.conkw.grabbersBase;

import net.pieroxy.conkw.api.metadata.grabberConfig.ConfigField;
import net.pieroxy.conkw.utils.hashing.Md5Sum;

import java.util.List;

public class PartiallyExtractableConfig {
  @ConfigField(
          label="List the active extractions. Have a look at the grabber documentation for the list of available values."
  )
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
