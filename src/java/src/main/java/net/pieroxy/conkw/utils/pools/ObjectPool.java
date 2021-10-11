package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.pools.inspectors.InspectorProvider;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspector;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspectorReport;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ObjectPool<T> {
    private final static Logger LOGGER = Logger.getLogger(ObjectPool.class.getName());
    private final static int TARGET_OVERCAPACITY_AS_PRC_OF_A_SECOND = 100; // 100 means we cap the pool to hold no less of 100% of 1s of throughput.

    private final ObjectPoolInspector<T> inspector;

    private final Queue<T> pool = new LinkedList<>();
    private long requested, created, recycled, wronglyRecycled, dropped, spawned;
    private int targetSize = 10;
    private double targetSizeStable = 10;
    private long lastRequested = 0;
    private long lastCheck = System.currentTimeMillis();
    private int minSize = 10;
    private int lastMinSize = 10;

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

        T res = inspector.giveOutInstance(result);
        minSize = Math.min(minSize, getPoolCurrentSize());
        return res;
    }

    private boolean shouldDrop() {
        int difference = minSize - (int)targetSizeStable;
        if (difference>2) { // 2 to prevent noise from dropping instances
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
        targetSizeStable = targetSizeStable*0.9 + targetSize/10.;
        lastRequested = requested;
        int miss = (int)targetSizeStable - minSize;
        if (miss>2) {
            miss=miss-2;

            if (LOGGER.isLoggable(Level.FINER)) LOGGER.finer("Adding "+miss+" instances");
            while (miss>0) {
                pool.add(createNewInstance());
                spawned++;
                miss--;
            }
        }
        lastMinSize = minSize;
        minSize = getPoolCurrentSize();
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
        return "ObjectPool:: R:" + getRequested() + " C:" + getCreated() + " R:" + getRecycled() + " wR:" + getWronglyRecycled() + " (-):" + dropped + " (+):" + spawned + " S:" + getPoolCurrentSize() + " mS:" + lastMinSize + " tS:" + targetSize + " tSS:"+((int)Math.round(targetSizeStable));
    }
}
