package net.pieroxy.conkw.utils.pools.inspectors;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectPoolInspectorReportItem {
    private final ThreadStack creation;
    private final ThreadStack violation;
    private final String message;
    private final String instanceDebugString;
    private int instances;

    public ObjectPoolInspectorReportItem(StackTraceElement[] creation, StackTraceElement[] violation, String message, String instanceDebugString, int instances) {
        this.creation = new ThreadStack(creation);
        this.violation = violation == null ? null : new ThreadStack(violation);
        this.message = message;
        this.instanceDebugString = instanceDebugString;
        this.instances = instances;
    }

    public ObjectPoolInspectorReportItem(ThreadStack creation, ThreadStack violation, String message, String instanceDebugString, int instances) {
        this.creation = creation;
        this.violation = violation;
        this.message = message;
        this.instanceDebugString = instanceDebugString;
        this.instances = instances;
    }

    public void log(Logger logger, Level level) {
        logger.log(level, "[" + instances + "] " + message);
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

    public int getInstances() {
        return instances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectPoolInspectorReportItem that = (ObjectPoolInspectorReportItem) o;
        return Objects.equals(creation, that.creation) && Objects.equals(violation, that.violation) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creation, violation, message);
    }

    public ObjectPoolInspectorReportItem addOneInstance() {
        instances++;
        return this;
    }
}
