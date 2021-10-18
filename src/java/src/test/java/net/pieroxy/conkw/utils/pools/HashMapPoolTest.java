package net.pieroxy.conkw.utils.pools;

import junit.framework.TestCase;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.utils.pools.hashmap.LightInstrumentedMap;
import net.pieroxy.conkw.utils.pools.inspectors.NoopInspector;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspectorReport;
import net.pieroxy.conkw.utils.pools.inspectors.ReusedRecycledObjectInspector;
import net.pieroxy.conkw.utils.pools.inspectors.UndisposedObjectsInspector;

import java.util.Map;
import java.util.TreeMap;

public class HashMapPoolTest extends TestCase {
    public void testCreation() {
        Map<?,?> map = null;
        ObjectPool<Map, Integer> pool = new HashMapPool((op) -> new NoopInspector());
        for (int i=0 ; i<100 ; i++) {
            map = pool.borrow(1);
        }
        assertEquals(100, pool.getCreated());
        assertEquals(100, pool.getRequested());
        assertEquals(0, pool.getRecycled());
        assertEquals(0, pool.getWronglyRecycled());
    }
    public void testReuse() {
        Map<?,?> map = null;
        ObjectPool<Map, Integer> pool = new HashMapPool((op) -> new NoopInspector());
        for (int i=0 ; i<100 ; i++) {
            pool.giveBack(map);
            map = pool.borrow(1);
        }
        assertEquals(0, pool.getWronglyRecycled()); // null is not counted as wrong.
        assertEquals(100, pool.getRequested());
        assertEquals(99, pool.getRecycled());
        assertEquals(1, pool.getCreated());
    }
    public void testWrongRecycle() {
        ObjectPool<Map, Integer> pool = new HashMapPool((op) -> new NoopInspector());
        pool.giveBack(new TreeMap());
        pool.giveBack(null);
        assertEquals(1, pool.getWronglyRecycled());
        assertEquals(0, pool.getRequested());
        assertEquals(0, pool.getRecycled());
        assertEquals(0, pool.getCreated());
    }
    public void testDebugNotRecycledObjects() {
        ObjectPool<Map, Integer> pool = new HashMapPool((op) -> new UndisposedObjectsInspector());
        Map m=null;
        for (int i=0 ; i<10 ; i++) {
            m = pool.borrow(1);
        }
        for (int i=0 ; i<10 ; i++) {
            m = pool.borrow(1);
            pool.giveBack(m);
        }
        pool.giveBack(new TreeMap());
        pool.giveBack(new LightInstrumentedMap());
        pool.giveBack(new LightInstrumentedMap());
        pool.giveBack(new LightInstrumentedMap());

        System.gc();
        UndisposedObjectsInspector inspector = (UndisposedObjectsInspector) pool.getInspector();
        assertEquals(3, inspector.getNotfound());
        assertEquals(1, pool.getWronglyRecycled());
        assertEquals(20, pool.getRequested());
        assertEquals(13, pool.getRecycled());
        assertEquals(11, pool.getCreated());
        ObjectPoolInspectorReport report = pool.getReport();
        assertEquals(4, report.getViolations().size());
        assertEquals(10, report.getViolations().iterator().next().getInstances());

    }

    public void testClearReturnedObjects() {
        ObjectPool<Map, Integer> pool = new HashMapPool((op) -> new NoopInspector());
        Map m = pool.borrow(1);
        assertEquals(0, m.size());
        m.put("a", "b");
        pool.giveBack(m);
        for (int i=0 ; i<10 ; i++) {
            m = pool.borrow(1);
            assertEquals(0, m.size());
        }
    }

    public void testAccessToGivenBackInstances() {
        ObjectPool<Map, Integer> pool = new HashMapPool((op) -> new ReusedRecycledObjectInspector());
        Map m = pool.borrow(1);
        pool.giveBack(m);
        ObjectPoolInspectorReport report = pool.getInspector().getReport();
        assertEquals(0, report.getViolations().size());
        m.size();
        report = pool.getInspector().getReport();
        assertEquals(1, report.getViolations().size());
    }
}
