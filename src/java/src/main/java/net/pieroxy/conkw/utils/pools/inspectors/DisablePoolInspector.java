package net.pieroxy.conkw.utils.pools.inspectors;

/**
 * This inspector just disables pooling. Useful to make performance comparisons.
 * @param <T>
 */
public class DisablePoolInspector<T> implements ObjectPoolInspector<T> {
  @Override
  public T giveOutInstance(T result) {
    return  result;
  }

  @Override
  public T recycle(T instanceToRecycle) {
    return null;
  }

  @Override
  public ObjectPoolInspectorReport getReport() {
    return null;
  }
}
