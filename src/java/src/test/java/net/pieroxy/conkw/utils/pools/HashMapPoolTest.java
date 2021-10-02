package net.pieroxy.conkw.utils.pools;

import junit.framework.TestCase;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspectorReport;
import net.pieroxy.conkw.utils.pools.inspectors.UndisposedObjectsInspector;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HashMapPoolTest extends TestCase {
    public void testCreation() {
        Map<?,?> map = null;
        ObjectPool<Map> pool = new HashMapPool(ObjectPoolBehavior.PROD);
        for (int i=0 ; i<100 ; i++) {
            map = pool.getNew();
        }
        assertEquals(100, pool.getCreated());
        assertEquals(100, pool.getRequested());
        assertEquals(0, pool.getRecycled());
        assertEquals(0, pool.getWronglyRecycled());
    }
    public void testReuse() {
        Map<?,?> map = null;
        ObjectPool<Map> pool = new HashMapPool(ObjectPoolBehavior.PROD);
        for (int i=0 ; i<100 ; i++) {
            pool.dispose(map);
            map = pool.getNew();
        }
        assertEquals(1, pool.getWronglyRecycled());
        assertEquals(100, pool.getRequested());
        assertEquals(99, pool.getRecycled());
        assertEquals(1, pool.getCreated());
    }
    public void testWrongRecycle() {
        ObjectPool<Map> pool = new HashMapPool(ObjectPoolBehavior.PROD);
        pool.dispose(new TreeMap());
        pool.dispose(null);
        assertEquals(2, pool.getWronglyRecycled());
        assertEquals(0, pool.getRequested());
        assertEquals(0, pool.getRecycled());
        assertEquals(0, pool.getCreated());
    }
    public void testDebugNotRecycledObjects() {
        ObjectPool<Map> pool = new HashMapPool(ObjectPoolBehavior.TRACK_NOT_DISPOSED);
        Map m=null;
        for (int i=0 ; i<10 ; i++) {
            m = pool.getNew();
        }
        for (int i=0 ; i<10 ; i++) {
            m = pool.getNew();
            pool.dispose(m);
        }
        pool.dispose(new TreeMap());
        pool.dispose(new HashMap());
        pool.dispose(new HashMap());
        pool.dispose(new HashMap());

        System.gc();
        UndisposedObjectsInspector inspector = (UndisposedObjectsInspector) pool.getInspector();
        assertEquals(3, inspector.getNotfound());
        assertEquals(1, pool.getWronglyRecycled());
        assertEquals(20, pool.getRequested());
        assertEquals(13, pool.getRecycled());
        assertEquals(11, pool.getCreated());
        ObjectPoolInspectorReport report = pool.getReport();
        assertEquals(1, report.getViolations().size());
        assertEquals(10, report.getViolations().iterator().next().getInstances());

    }
}
