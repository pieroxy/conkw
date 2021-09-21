package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.webapp.model.ResponseData;

public interface SimpleCollector extends Collector {
  void setData(ResponseData data);
  void setData(SimpleCollector data);
}
