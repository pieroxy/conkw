package net.pieroxy.conkw.utils.pools.inspectors;

import java.lang.ref.WeakReference;
import java.util.*;

public class UndisposedObjectsInspector<T> implements ObjectPoolInspector<T> {
    Collection<ObjectWithContext<WeakReference<T>>> instances = new LinkedList<>();
    Collection<ThreadStack> notfound = new ArrayList<>();

    @Override
    public synchronized T giveOutInstance(T result) {
        instances.add(new ObjectWithContext<>(new WeakReference(result)));
        return result;
    }

    @Override
    public synchronized T recycle(T instanceToRecycle) {
        if (instanceToRecycle == null) return null;
        for (ObjectWithContext<WeakReference<T>> elem : instances) {
            if (elem.getInstance().get() == instanceToRecycle) {
                instances.remove(elem);
                return instanceToRecycle;
            }
        }
        notfound.add(new ThreadStack(Thread.currentThread().getStackTrace()));
        return instanceToRecycle;
    }

    @Override
    public synchronized ObjectPoolInspectorReport getReport() {
        System.gc();
        try { Thread.sleep(20); } catch (Exception e) {} // This is just so that the gc log gets flushed before this report gets logged.
        ObjectPoolInspectorReport res = new ObjectPoolInspectorReport();
        res.setGlobalStatus("inspected objects: " + instances.size() + ", Returned instances not found: " + notfound.size());
        Collection<ObjectWithContext<WeakReference<T>>> newinstances = new LinkedList<>();
        Map<ThreadStack, Integer> stackTraceCount = new HashMap<>();
        for (ObjectWithContext<WeakReference<T>> c : instances) {
            if (c.getInstance().get() == null) {
                stackTraceCount.put(c.getCallStack(), stackTraceCount.getOrDefault(c.getCallStack(), 0)+1);
            } else {
                newinstances.add(c);
            }
        }
        stackTraceCount.entrySet().forEach(e -> {
            res.addViolation(new ObjectPoolInspectorReportItem(e.getKey().getStack(), null, "Object was obtained from pool and garbaged collected.", "", e.getValue()));
        });
        stackTraceCount.clear();
        for (ThreadStack c : notfound) {
            stackTraceCount.put(c, stackTraceCount.getOrDefault(c, 0)+1);
        }
        notfound.clear();
        stackTraceCount.entrySet().forEach(e -> {
            res.addViolation(new ObjectPoolInspectorReportItem(null, e.getKey().getStack(), "Object was given back to the pool but did not originate from the pool.", "", e.getValue()));
        });

        instances = newinstances;
        return res;
    }

    public int getNotfound() {
        return notfound.size();
    }
}
