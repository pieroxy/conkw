package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.pools.inspectors.ThreadStack;

public interface ObservableObject {
  boolean hasBeenAccessed();
  void resetAccessedValue();
  ThreadStack getAccessedStack();
}
