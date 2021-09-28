package net.pieroxy.conkw.utils.pools.inspectors;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectPoolInspectorReportItem {
    private StackTraceElement[] creation;
    private StackTraceElement[] violation;
    String message;
    String instanceDebugString;

    public ObjectPoolInspectorReportItem(StackTraceElement[] creation, StackTraceElement[] violation, String message, String instanceDebugString) {
        this.creation = creation;
        this.violation = violation;
        this.message = message;
        this.instanceDebugString = instanceDebugString;
    }

    public void log(Logger logger, Level level) {
        // TO BE IMPLEMENTED
    }
}
