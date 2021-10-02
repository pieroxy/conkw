package net.pieroxy.conkw.utils.pools.inspectors;

import net.pieroxy.conkw.utils.duration.CDuration;

import java.io.Closeable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegularReportInspector<T> implements ObjectPoolInspector<T> {
  private final static Logger LOGGER = Logger.getLogger(RegularReportInspector.class.getName());

  private final ObjectPoolInspector<T> inspector;
  private final CDuration interval;
  private long lastReport;

  public RegularReportInspector(ObjectPoolInspector<T> inspector, CDuration interval) {
    this.inspector = inspector;
    this.interval = interval;
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
      getReport().getViolations().forEach(v -> v.log(LOGGER, Level.WARNING));
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
