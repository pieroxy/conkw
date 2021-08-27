package net.pieroxy.conkw.webapp.grabbers.logfile;

import java.util.Date;
import java.util.Map;

public interface LogRecord {
    String COUNT = "count";

    boolean isValid();
    Map<String, String> getDimensions();
    Map<String, Double> getValues();
    String getName();
}
