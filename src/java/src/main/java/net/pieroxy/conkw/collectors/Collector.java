package net.pieroxy.conkw.collectors;

import java.util.Map;

public interface Collector {
    String DEFAULT_METRIC_NAME="metric";
    String DEFAULT_METRIC_VALUE="value";

    void collect(String metric, Map<String, String> dimensions, Map<String, Double> values);
    void collect(String metric, double value);
}
