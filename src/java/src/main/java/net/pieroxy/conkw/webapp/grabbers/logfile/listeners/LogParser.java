package net.pieroxy.conkw.webapp.grabbers.logfile.listeners;

import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;

public interface LogParser<T extends LogRecord> {
  T parse(String line);
}
