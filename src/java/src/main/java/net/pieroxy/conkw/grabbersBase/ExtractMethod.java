package net.pieroxy.conkw.grabbersBase;

import net.pieroxy.conkw.collectors.Collector;

public interface ExtractMethod<T extends Collector> {
  void extract(T res);
}
