package net.pieroxy.conkw.utils.pools.inspectors;

public class ObjectWithContext<T> {
    ThreadStack callStack;
    T instance;
    long timestamp;

    public ObjectWithContext(T instance) {
        this.instance = instance;
        this.callStack = new ThreadStack(Thread.currentThread().getStackTrace());
        this.timestamp = System.currentTimeMillis();
    }

    T getInstance() {
        return instance;
    }

    ThreadStack getCallStack() {
        return callStack;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
