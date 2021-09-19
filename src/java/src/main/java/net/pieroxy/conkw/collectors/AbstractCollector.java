package net.pieroxy.conkw.collectors;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCollector implements Collector {

    @Override
    public void collect(String metric, double value) {
        collect(metric, null, getMap(DEFAULT_METRIC_VALUE, value));
    }

    <T> Map<String, T> getMap(String name, T value) {
        Map<String, T> res = new HashMap<>();
        res.put(name, value);
        return res;
    }
}
