package net.pieroxy.conkw.webapp.grabbers.logfile.listeners;

/**
 * A log listener is a class that can listen to a log file. This means implementing the newLine method,
 * that takes as a parameter the time of the processing and the line to process.
 */
public interface LogListener<T> {
  void newLine(long time, T line);
}
