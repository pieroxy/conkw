package net.pieroxy.conkw.utils.pools.inspectors;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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
        Map<ThreadStack, Integer> stackTraceCount = new HashMap<>();
        for (ObjectWithContext<T> c : instances) {
            if (c.getInstance() == null) {
                stackTraceCount.put(c.getCallStack(), stackTraceCount.getOrDefault(c.getCallStack(), 0)+1);
            } else {
                newinstances.add(c);
            }
        }
        stackTraceCount.entrySet().forEach(e -> {
            res.addViolation(new ObjectPoolInspectorReportItem(e.getKey().getStack(), null, "Object was obtained from pool and garbaged collected.", "", e.getValue()));
        });
        instances = newinstances;
        return res;
    }
}
