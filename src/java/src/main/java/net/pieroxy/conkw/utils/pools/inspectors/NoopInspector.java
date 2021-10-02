package net.pieroxy.conkw.utils.pools.inspectors;

public final class NoopInspector<T> implements ObjectPoolInspector<T> {
    @Override
    public T giveOutInstance(T result) {
        return result;
    }

    @Override
    public T recycle(T instanceToRecycle) {
        return instanceToRecycle;
    }

    @Override
    public ObjectPoolInspectorReport getReport() {
        return new ObjectPoolInspectorReport();
    }
}
