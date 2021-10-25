package net.pieroxy.conkw.pub.mdlog;

public interface LogParser<T extends LogRecord> {
  T parse(String line);
}
