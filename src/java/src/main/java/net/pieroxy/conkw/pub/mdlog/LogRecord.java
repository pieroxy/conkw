package net.pieroxy.conkw.pub.mdlog;

import java.util.Map;

public interface LogRecord {
    String COUNT = "count";

    boolean isValid();
    Map<String, String> getDimensions();
    Map<String, Double> getValues();
    String getName();
}
