package net.pieroxy.conkw.utils.pools.inspectors;

import java.util.Collection;
import java.util.LinkedList;

public class ObjectPoolInspectorReport {
    private Collection<ObjectPoolInspectorReportItem> violations;

    public ObjectPoolInspectorReport() {
        this.violations = new LinkedList<>();
    }

    void addViolation(ObjectPoolInspectorReportItem item) {
        violations.add(item);
    }

    public Collection<ObjectPoolInspectorReportItem> getViolations() {
        return violations;
    }
}
