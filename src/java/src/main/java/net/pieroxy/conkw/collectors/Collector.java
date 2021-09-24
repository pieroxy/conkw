package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.Collection;

public interface Collector {
    String DEFAULT_METRIC_NAME="metric";
    String DEFAULT_METRIC_VALUE="value";

    /**
     * Returns data collected.
     * @return
     */
    ResponseData getData();

    /**
     * Returns data collected.
     * @return
     */
    Collection<String> getErrors();

    /**
     * Prepare for the coming of new data. The collector should start a new "session".
     * @return
     */
    void prepareForCollection();

    /**
     * Notify the grabber that the current collection is done.
     * @return
     */
    void collectionDone();


    /**
     *
     * @param time The time taken to grab data in this collector.
     */
    void setTime(long time);

    /**
     *
     * @param time The time at which the data in this collector was written/extracted.
     */
    void setTimestamp(long time);

    /**
     *
     * @return The time taken to grab data in this collector.
     */
    long getTime();

    /**
     * Add an error message to be displayed to clients.
     * @param message The error message.
     */
    void addError(String message);

    /**
     *
     * @return True if at least one error was added to this collector.
     */
    boolean hasError();

    /**
     *
     * @return The time at which the data in this collector was written/extracted.
     */
    long getTimestamp();

    /**
     * Add a simple key/value metric to this collector.
     * @param metric The metric name or key
     * @param value The metric value
     */
    void collect(String metric, double value);
    void collect(String metric, String value);

    /**
     *
     * @return A string identifying this collector. For configurable grabbers this is a parameter as to what is supposed
     * to be grabbed or a parameter as to how it is supposed to be aggregated.
     */
    String getConfigKey();
}
