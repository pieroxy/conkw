package net.pieroxy.conkw.pub.mdlog;

public interface LogParser<T extends DataRecord> {
  T parse(String line);
}
