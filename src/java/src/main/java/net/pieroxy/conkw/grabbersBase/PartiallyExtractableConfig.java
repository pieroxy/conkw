package net.pieroxy.conkw.grabbersBase;

import java.util.List;

public interface PartiallyExtractableConfig {
  List<String> getToExtract();
  void setToExtract(List<String> s);
}
