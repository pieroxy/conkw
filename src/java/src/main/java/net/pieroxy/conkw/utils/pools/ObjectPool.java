package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.pools.inspectors.InspectorProvider;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspector;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspectorReport;

import java.util.LinkedList;
import java.util.Queue;
import java.util.SplittableRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ObjectPool<T> {
    private final static Logger LOGGER = Logger.getLogger(ObjectPool.class.getName());
    private final static int TARGET_OVERCAPACITY_AS_PRC_OF_A_SECOND = 100; // 100 means we cap the pool to hold no more of 1s of throughput.

    private final ObjectPoolInspector<T> inspector;
    private final SplittableRandom random = new SplittableRandom();

    private final Queue<T> pool = new LinkedList<>();
    private long requested, created, recycled, wronglyRecycled, dropped;
    private int targetSize = 10;
    private long lastRequested = 0;
    private long lastCheck = System.currentTimeMillis();

    protected abstract T createNewInstance();
    protected abstract T getInstanceToRecycle(T instance);

    protected ObjectPool(InspectorProvider behavior) {
       this.inspector = behavior.get(this);
    }

    public synchronized T borrow() {
        T result = pool.poll();
        requested++;
        if (System.currentTimeMillis() - lastCheck > 1000) {
            lastCheck = System.currentTimeMillis();
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.log(Level.FINE, getDebugString());
            }
            computeTargetSize();
        }
        if (result == null) {
            created++;
            result = createNewInstance();
        }
        return inspector.giveOutInstance(result);
    }

    private boolean shouldDrop() {
        int difference = getPoolCurrentSize() - targetSize;
        if (difference>0 && (random.nextDouble()/difference < 0.01)) {
            if (LOGGER.isLoggable(Level.FINER)) LOGGER.finer("Dropping an instance, difference=" + difference);
            return true;
        }
        return false;
    }

    public synchronized void giveBack(T instance) {
        instance = inspector.recycle(getInstanceToRecycle(instance));
        if (instance!=null) {
            if (shouldDrop()) {
                dropped++;
            } else {
                recycled++;
                pool.add(instance);
            }
        } else {
            wronglyRecycled++;
        }
    }

    private void computeTargetSize() {
        targetSize = (int)(requested-lastRequested)*100/TARGET_OVERCAPACITY_AS_PRC_OF_A_SECOND;
        lastRequested = requested;
        int miss = targetSize - getPoolCurrentSize();
        // Slowly increase the size to the target.
        // This is because the target is recomputed every second and is thus prone to noise.
        // If we add too quickly, we'll end up dropping as fast and it serves little purpose.
        if (miss>0 && random.nextDouble()/miss < 0.01) {
            if (LOGGER.isLoggable(Level.FINER)) LOGGER.finer("Added an instance, miss=" + miss);
            pool.add(createNewInstance());
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
