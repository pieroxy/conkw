package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspector;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspectorReport;

import java.util.LinkedList;
import java.util.Queue;

public abstract class ObjectPool<T> {
    private final ObjectPoolInspector<T> inspector;
    private final Queue<T> pool = new LinkedList<>();
    private long requested, created, recycled, wronglyRecycled;

    protected abstract T createNewInstance();
    protected abstract T getInstanceToRecycle(T instance);

    protected ObjectPool(ObjectPoolBehavior behavior) {
       this.inspector = behavior.getInspector();
    }

    public synchronized T getNew() {
        T result = pool.poll();
        requested++;
        if (result == null) {
            created++;
            result = createNewInstance();
        }
        return inspector.giveOutInstance(result);
    }

    public synchronized void dispose(T instance) {
        instance = inspector.recycle(getInstanceToRecycle(instance));
        if (instance!=null) {
            recycled++;
            pool.add(instance);
        } else {
            wronglyRecycled++;
        }
    }

    public long getRequested() {
        return requested;
    }

    public long getCreated() {
        return created;
    }

    public long getRecycled() {
        return recycled;
    }

    public long getWronglyRecycled() {
        return wronglyRecycled;
    }

    public ObjectPoolInspectorReport getReport() {
        return inspector.getReport();
    }
}
