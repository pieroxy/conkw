package net.pieroxy.conkw.utils.pools.inspectors;

import java.util.Collection;
import java.util.LinkedList;

public class UndisposedObjectsInspector<T> implements ObjectPoolInspector<T> {
    Collection<ObjectWithContext<T>> instances = new LinkedList<>();


    @Override
    public synchronized T giveOutInstance(T result) {
        instances.add(new ObjectWithContext<>(result));
        return result;
    }

    @Override
    public synchronized T recycle(T instanceToRecycle) {
        if (instanceToRecycle == null) return null;
        for (ObjectWithContext<T> elem : instances) {
            if (elem.getInstance() == instanceToRecycle) {
                instances.remove(elem);
                break;
            }
        }
        return instanceToRecycle;
    }

    @Override
    public synchronized ObjectPoolInspectorReport getReport() {
        ObjectPoolInspectorReport res = new ObjectPoolInspectorReport();
        Collection<ObjectWithContext<T>> newinstances = new LinkedList<>();
        for (ObjectWithContext<T> c : instances) {
            if (c.getInstance() == null) {
                res.addViolation(new ObjectPoolInspectorReportItem(c.getCallStack(), null, "Object was obtained from pool and garbaged collected.", ""));
            } else {
                newinstances.add(c);
            }
        }
        instances = newinstances;
        return res;
    }
}
