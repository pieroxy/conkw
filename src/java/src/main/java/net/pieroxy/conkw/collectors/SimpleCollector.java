package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.webapp.model.ResponseData;

public interface SimpleCollector extends Collector {
  void copyDataFrom(ResponseData data);
  void copyDataFrom(SimpleCollector data);
  void setInitialized(boolean initialized);

}
