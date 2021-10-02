package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspector;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspectorReport;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ObjectPool<T> {
    private final static Logger LOGGER = Logger.getLogger(ObjectPool.class.getName());
    private final static int TARGET_OVERCAPACITY = 5; // Also by logic, the minimum capacity.

    private final ObjectPoolInspector<T> inspector;
    private final Queue<T> pool = new LinkedList<>();
    private long requested, created, recycled, wronglyRecycled, dropped;
    private int targetSize = 10;
    private int transientMinPoolSize = 10;
    private int transientMissed = 0;

    protected abstract T createNewInstance();
    protected abstract T getInstanceToRecycle(T instance);

    protected ObjectPool(ObjectPoolBehavior behavior) {
       this.inspector = behavior.getInspector(this);
    }

    public synchronized T getNew() {
        T result = pool.poll();
        requested++;
        if (LOGGER.isLoggable(Level.FINE) && requested%100 == 0) {
            LOGGER.log(Level.FINE, getDebugString());
        }
        if (result == null) {
            created++;
            transientMissed++;
            transientMinPoolSize=0;
            result = createNewInstance();
        } else {
            transientMinPoolSize = Math.min(transientMinPoolSize, getPoolCurrentSize());
        }
        return inspector.giveOutInstance(result);
    }

    public synchronized void dispose(T instance) {
        instance = inspector.recycle(getInstanceToRecycle(instance));
        if (instance!=null) {
            if (pool.size() > targetSize) {
                dropped++;
            } else {
                recycled++;
                pool.add(instance);
            }
            if ((recycled+dropped)%100 == 0) {
                computeTargetSize();
            }
        } else {
            wronglyRecycled++;
        }
    }

    private void computeTargetSize() {
        if (transientMissed > 0) {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Increasing target size by number of missed items: " + transientMissed);
            }
            targetSize+=transientMissed;
        } else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, "Adjusting target size according to minimum size ("+transientMinPoolSize+"): " + (TARGET_OVERCAPACITY-transientMinPoolSize));
            }
            targetSize = Math.max(TARGET_OVERCAPACITY, targetSize + TARGET_OVERCAPACITY - transientMinPoolSize);
        }
        transientMissed = 0;
        transientMinPoolSize = getPoolCurrentSize();
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

    public int getPoolCurrentSize() {
        return pool.size();
    }

    public ObjectPoolInspectorReport getReport() {
        return inspector.getReport();
    }

    public ObjectPoolInspector<T> getInspector() {
        return inspector;
    }

    public String getDebugString() {
        return "ObjectPool report :: requested:" + getRequested() + " created:" + getCreated() + " recycled:" + getRecycled() + " wrongRecycled:" + getWronglyRecycled() + " dropped:" + dropped + " currentSize:" + getPoolCurrentSize() + " targetSize:" + targetSize;
    }
}
