package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspector;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspectorReport;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ObjectPool<T> {
    private final static Logger LOGGER = Logger.getLogger(ObjectPool.class.getName());
    private final static boolean DEBUG = true;

    private final ObjectPoolInspector<T> inspector;
    private final Queue<T> pool = new LinkedList<>();
    private long requested, created, recycled, wronglyRecycled;

    protected abstract T createNewInstance();
    protected abstract T getInstanceToRecycle(T instance);

    protected ObjectPool(ObjectPoolBehavior behavior) {
       this.inspector = behavior.getInspector(this);
    }

    public synchronized T getNew() {
        T result = pool.poll();
        requested++;
        if (DEBUG && requested%100 == 0) {
            LOGGER.log(Level.INFO, getDebugString());
        }
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

    public long getPoolCurrentSize() {
        return pool.size();
    }

    public ObjectPoolInspectorReport getReport() {
        return inspector.getReport();
    }

    public ObjectPoolInspector<T> getInspector() {
        return inspector;
    }

    public String getDebugString() {
        return "ObjectPool report :: requested:" + getRequested() + " created:" + getCreated() + " recycled:" + getRecycled() + " wrongRecycled:" + getWronglyRecycled() + " currentSize:" + getPoolCurrentSize();
    }
}
