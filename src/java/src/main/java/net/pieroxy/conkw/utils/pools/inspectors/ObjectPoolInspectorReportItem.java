package net.pieroxy.conkw.utils.pools.inspectors;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectPoolInspectorReportItem {
    private ThreadStack creation;
    private ThreadStack violation;
    String message;
    String instanceDebugString;

    public ObjectPoolInspectorReportItem(StackTraceElement[] creation, StackTraceElement[] violation, String message, String instanceDebugString) {
        this.creation = new ThreadStack(creation);
        this.violation = violation == null ? null : new ThreadStack(violation);
        this.message = message;
        this.instanceDebugString = instanceDebugString;
    }

    public void log(Logger logger, Level level) {
        logger.log(level, message);
        if (creation!=null && !creation.isEmpty()) {
            logger.log(level, "Creation:");
            for (StackTraceElement e : creation.getStack()) {
                logger.log(level, "  -> " + e);
            }
        }
        if (violation!=null && !violation.isEmpty()) {
            logger.log(level, "Violation:");
            for (StackTraceElement e : violation.getStack()) {
                logger.log(level, "  -> " + e);
            }
        }
    }
}
