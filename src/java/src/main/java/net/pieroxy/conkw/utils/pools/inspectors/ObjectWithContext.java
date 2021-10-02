package net.pieroxy.conkw.utils.pools.inspectors;

import java.lang.ref.WeakReference;

public class ObjectWithContext<T> {
    ThreadStack callStack;
    WeakReference<T> instance;
    long timestamp;

    public ObjectWithContext(T instance) {
        this.instance = new WeakReference<>(instance);
        this.callStack = new ThreadStack(Thread.currentThread().getStackTrace());
        this.timestamp = System.currentTimeMillis();
    }

    T getInstance() {
        return instance.get();
    }

    ThreadStack getCallStack() {
        return callStack;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
