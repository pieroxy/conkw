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
     * Prepare for the coming of new data. The collector should start a new "session".
     * @return
     */
    void prepareForCollection();

    void setTime(long time);
    void setTimestamp(long time);

    long getTime();

    void addError(String message);

    boolean hasError();

    long getTimestamp();

    void collect(String metric, double value);
    void collect(String metric, String value);

}
