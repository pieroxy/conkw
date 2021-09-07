package net.pieroxy.conkw;

import junit.framework.TestCase;

import java.util.Map;

public abstract class ConkwTestCase extends TestCase {
    public void assertMapContains(Map<?,?> data, Object key, Object value) {
        if (data.containsKey(key)) {
            assertEquals(value, data.get(key));
        } else {
            String msg = "Key '"+key+"' not found. Keys in the map["+data.size()+"]: ";
            for (Object o : data.keySet()) {
                msg += "'" + o + "' ";
            }
            fail(msg);
        }
    }

    public  <T extends Throwable> void assertThrows(Runnable r, Class<T> shouldThrow, ExceptionInspector<T> inspector) {
        try {
            r.run();
        } catch (Exception e) {
            assertEquals(shouldThrow, e.getClass());
            if (inspector != null) inspector.inspect((T)e);
            return;
        }
        fail("Expected to throw a " + shouldThrow.getSimpleName());
    }

    public static interface ExceptionInspector<T extends Throwable> {
        void inspect(T exception);
    }
}
