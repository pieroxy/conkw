package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.webapp.model.ResponseData;

public interface Collector {
    String DEFAULT_METRIC_NAME="metric";
    String DEFAULT_METRIC_VALUE="value";

    /**
     * Returns data collected.
     * @return
     */
    ResponseData getData();

    /**
     * Reset this collector to its virgin state.
     * @return
     */
    void reset();

    void setTime(long time);

    long getTime();

    void addError(String message);

    boolean hasError();

    long getTimestamp();
}
