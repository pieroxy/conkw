package net.pieroxy.conkw.pub.mdlog;

import net.pieroxy.conkw.pub.misc.ConkwCloseable;

import java.util.Map;

public interface LogRecord extends ConkwCloseable {
    String COUNT = "count";

    Map<String, String> getDimensions();
    Map<String, Double> getValues();
}
