package net.pieroxy.conkw.pub.mdlog;

import java.util.Map;

public interface LogRecord {
    String COUNT = "count";

    Map<String, String> getDimensions();
    Map<String, Double> getValues();
}
