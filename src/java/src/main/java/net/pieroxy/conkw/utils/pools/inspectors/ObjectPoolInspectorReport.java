package net.pieroxy.conkw.utils.pools.inspectors;

import java.util.Collection;
import java.util.LinkedList;

public class ObjectPoolInspectorReport {
    private Collection<ObjectPoolInspectorReportItem> violations;
    private String globalStatus;

    public ObjectPoolInspectorReport() {
        this.violations = new LinkedList<>();
    }

    void addViolation(ObjectPoolInspectorReportItem item) {
        violations.add(item);
    }

    public Collection<ObjectPoolInspectorReportItem> getViolations() {
        return violations;
    }

    public String getGlobalStatus() {
        return globalStatus;
    }

    public void setGlobalStatus(String globalStatus) {
        this.globalStatus = globalStatus;
    }
}
