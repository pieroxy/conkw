package net.pieroxy.conkw;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public <K,V> void assertMapEquals(Map<K,V> values2, Map<K,V> values1) {
        String keys2 = values2.keySet().stream().map(String::valueOf).sorted().collect(Collectors.joining(","));
        String keys1 = values1.keySet().stream().map(String::valueOf).sorted().collect(Collectors.joining(","));
        if (!Objects.equals(keys1, keys2)) {
            fail("Expected keys to be " +
                    keys2 +
                    " but were " +
                    keys1);
        }
        values2.entrySet().stream().forEach(e->assertMapContains(values1, e.getKey(), e.getValue()));

    }

    public static interface ExceptionInspector<T extends Throwable> {
        void inspect(T exception);
    }

}
