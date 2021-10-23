package net.pieroxy.conkw.pub.mdlog;

import net.pieroxy.conkw.pub.mdlog.LogRecord;

public interface LogParser<T extends LogRecord> {
  T parse(String line);
}
