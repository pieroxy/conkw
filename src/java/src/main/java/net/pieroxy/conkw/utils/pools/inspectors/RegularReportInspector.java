package net.pieroxy.conkw.utils.pools.inspectors;

import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.pools.ObjectPool;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegularReportInspector<T> implements ObjectPoolInspector<T> {
  private final static Logger LOGGER = Logger.getLogger(RegularReportInspector.class.getName());

  private final ObjectPoolInspector<T> inspector;
  private final CDuration interval;
  private final ObjectPool pool;
  private long lastReport;

  public RegularReportInspector(ObjectPoolInspector<T> inspector, CDuration interval, ObjectPool pool) {
    this.inspector = inspector;
    this.interval = interval;
    this.pool = pool;
    this.lastReport = System.currentTimeMillis();
  }

  @Override
  public T giveOutInstance(T result) {
    check();
    return inspector.giveOutInstance(result);
  }

  private void check() {
    if (interval.isExpired(lastReport, System.currentTimeMillis())) {
      lastReport = System.currentTimeMillis();
      ObjectPoolInspectorReport report = getReport();
      LOGGER.log(Level.WARNING, pool.getDebugString());
      LOGGER.log(Level.WARNING, report.getGlobalStatus());
      report.getViolations().forEach(v -> v.log(LOGGER, Level.WARNING));
    }
  }

  @Override
  public T recycle(T instanceToRecycle) {
    return inspector.recycle(instanceToRecycle);
  }

  @Override
  public ObjectPoolInspectorReport getReport() {
    return inspector.getReport();
  }
}
