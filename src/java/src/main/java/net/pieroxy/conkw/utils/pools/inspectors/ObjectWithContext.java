package net.pieroxy.conkw.utils.pools.inspectors;

import java.lang.ref.WeakReference;

public class ObjectWithContext<T> {
    StackTraceElement[] callStack;
    WeakReference<T> instance;
    long timestamp;

    public ObjectWithContext(T instance) {
        this.instance = new WeakReference<>(instance);
        this.callStack = Thread.currentThread().getStackTrace();
        this.timestamp = System.currentTimeMillis();
    }

    T getInstance() {
        return instance.get();
    }

    StackTraceElement[] getCallStack() {
        return callStack;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
