package net.pieroxy.conkw;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Map;

public abstract class ConkwTestCase extends TestCase {
    public  <K,V> void assertMapContains(Map<K,V> data, K key, V value) {
        if (data.containsKey(key)) {
            assertEquals("Looking for key " + key, value, data.get(key));
        } else {
            String msg = "Key '"+key+"' not found. Keys in the map["+data.size()+"]: ";
            for (Object o : data.keySet()) {
                msg += "'" + o + "' ";
            }
            fail(msg);
        }
    }

    public <K> void assertMapDoesNotContain(Map<K,?> data, K key) { {
        if (data.containsKey(key)) {
            String msg = "Key '"+key+"' found in map with value '"+data.get(key)+"'";
            fail(msg);
        }
    }}

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

    public void assertContainsStack(StackTraceElement[]array, StackTraceElement e, int pos) {
        if (!containsStack(array, e, pos)) {
            Arrays.stream(array).forEach(System.out::println);
            fail("Expected stack trace to contain " + e.toString());
        }
    }

    public void assertNotContainsStack(StackTraceElement[]array, StackTraceElement e, int pos) {
        if (containsStack(array, e, pos)) {
            fail("Expected stack trace to *not* contain " + e.toString());
        }
    }

    private boolean containsStack(StackTraceElement[] array, StackTraceElement e, int pos) {
        for (StackTraceElement ste : array) {
            if (ste.getClassName().equals(e.getClassName()) &&
                ste.getFileName().equals(e.getFileName()) &&
                ste.getLineNumber() == e.getLineNumber() &&
                ste.getMethodName().equals(e.getMethodName())) return true;
        }

        return false;
    }

    public static interface ExceptionInspector<T extends Throwable> {
        void inspect(T exception);
    }

}
