package net.pieroxy.conkw.utils.pools;

public interface ObservableObject {
  boolean hasBeenAccessed();
  void resetAccessedValue();
}
