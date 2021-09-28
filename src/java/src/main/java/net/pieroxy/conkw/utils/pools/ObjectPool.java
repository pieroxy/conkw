package net.pieroxy.conkw.utils.pools;

import java.util.LinkedList;
import java.util.Queue;

public abstract class ObjectPool<T> {
    private final Queue<T> pool = new LinkedList<>();
    private long requested, created, recycled, wronglyRecycled;

    protected abstract T createNewInstance();
    protected abstract T getInstanceToRecycle(T instance);

    public synchronized T getNew() {
        T result = pool.poll();
        requested++;
        if (result == null) {
            created++;
            result = createNewInstance();
        }
        return result;
    }

    public synchronized void dispose(T instance) {
        instance = getInstanceToRecycle(instance);
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
}
