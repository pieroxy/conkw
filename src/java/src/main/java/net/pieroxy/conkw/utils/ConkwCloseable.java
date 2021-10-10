package net.pieroxy.conkw.utils;

public interface ConkwCloseable extends AutoCloseable {
  @Override
  void close();
}
