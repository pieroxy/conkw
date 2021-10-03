package net.pieroxy.conkw.utils.pools.inspectors;

import net.pieroxy.conkw.utils.pools.hashmap.InstrumentedHashMap;

import java.util.*;

public class ReusedRecycledObjectInspector implements ObjectPoolInspector<Map> {
  private static final long TTL = 60000; // We keep all instances for 1min to check for access violations
  Collection<ObjectWithContext<InstrumentedHashMap>> recycled = new LinkedList<>();

  @Override
  public Map giveOutInstance(Map result) {
    return new InstrumentedHashMap(result);
  }

  @Override
  public synchronized Map recycle(Map instanceToRecycle) {
    if (instanceToRecycle==null || !(instanceToRecycle instanceof InstrumentedHashMap)) return null;
    InstrumentedHashMap im = (InstrumentedHashMap)instanceToRecycle;
    recycled.add(new ObjectWithContext<>(im));
    im.resetAccessedValue();
    return null;
  }

  @Override
  public synchronized ObjectPoolInspectorReport getReport() {
    ObjectPoolInspectorReport res = new ObjectPoolInspectorReport();
    Iterator<ObjectWithContext<InstrumentedHashMap>> it = recycled.iterator();
    Map<ObjectPoolInspectorReportItem, ObjectPoolInspectorReportItem> violations = new HashMap<>();
    while (it.hasNext()) {
      ObjectWithContext<InstrumentedHashMap> next = it.next();
      if (next.getInstance().hasBeenAccessed()) {
        ObjectPoolInspectorReportItem item = new ObjectPoolInspectorReportItem(next.callStack, next.getInstance().getAccessedStack(), "Object was accessed after being recycled", "", 1);
        violations.put(item, violations.getOrDefault(item, item).addOneInstance());
        it.remove();
      } else if (next.getTimestamp() > System.currentTimeMillis() + TTL) {
        it.remove();
      }
    }
    violations.values().forEach(res::addViolation);
    return res;
  }
}
