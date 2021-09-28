package net.pieroxy.conkw.utils.pools.inspectors;

public interface ObjectPoolInspector<T> {
    T giveOutInstance(T result);
    T recycle(T instanceToRecycle);
    ObjectPoolInspectorReport getReport();
}
