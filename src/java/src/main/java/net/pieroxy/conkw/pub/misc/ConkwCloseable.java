package net.pieroxy.conkw.pub.misc;

public interface ConkwCloseable extends AutoCloseable {
  @Override
  void close();
}
