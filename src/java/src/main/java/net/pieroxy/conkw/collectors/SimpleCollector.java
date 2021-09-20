package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.webapp.model.ResponseData;

public interface SimpleCollector extends Collector {
  void collect(String metric, double value);
  void collect(String metric, String value);
  void setData(ResponseData data);
}
