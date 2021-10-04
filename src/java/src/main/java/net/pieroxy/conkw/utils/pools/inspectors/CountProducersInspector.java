package net.pieroxy.conkw.utils.pools.inspectors;

import java.lang.ref.WeakReference;
import java.util.*;

public class CountProducersInspector<T> implements ObjectPoolInspector<T> {
    Collection<ObjectWithContext<T>> instances = new ArrayList<>();
    private final int maxStackDepth;

    public CountProducersInspector(int maxStackDepth) {
        this.maxStackDepth = maxStackDepth;
    }

    @Override
    public T giveOutInstance(T result) {
        instances.add(new ObjectWithContext<>(result, 5, maxStackDepth));
        return result;
    }

    @Override
    public T recycle(T instanceToRecycle) {
        return instanceToRecycle;
    }

    @Override
    public ObjectPoolInspectorReport getReport() {
        ObjectPoolInspectorReport res = new ObjectPoolInspectorReport();
        res.setGlobalStatus("inspected objects: " + instances.size());
        Map<ThreadStack, Integer> stackTraceCount = new HashMap<>();
        for (ObjectWithContext<T> c : instances) {
            stackTraceCount.put(c.getCallStack(), stackTraceCount.getOrDefault(c.getCallStack(), 0)+1);
        }
        stackTraceCount.entrySet().forEach(e -> {
            res.addViolation(new ObjectPoolInspectorReportItem(e.getKey().getStack(), null, "Object borrowed from pool.", "", e.getValue()));
        });
        instances.clear();
        return res;
    }
}
