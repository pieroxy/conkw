package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.utils.pools.inspectors.ThreadStack;

public class ThreadStackTest extends ConkwTestCase {
    public void testSimple() {
        ThreadStack stack = new ThreadStack(Thread.currentThread().getStackTrace());
        assertContainsStack(stack.getStack(), new StackTraceElement(this.getClass().getName(), "testSimple", "ThreadStackTest.java", 8), 2);
        assertContainsStack(getStack().getStack(), new StackTraceElement(this.getClass().getName(), "testSimple", "ThreadStackTest.java", 10), 2);
        assertContainsStack(getStack().getStack(), new StackTraceElement(this.getClass().getName(), "getStack", "ThreadStackTest.java", 15), 2);
    }

    private ThreadStack getStack() {
        return new ThreadStack(Thread.currentThread().getStackTrace());
    }

    public void testNot() {
        assertNotContainsStack(Thread.currentThread().getStackTrace(), new StackTraceElement(this.getClass().getName(), "testSimple", "ThreadStackTest.java", 8), 0);
    }

    public void testTruncate() {
        ThreadStack ts = getStack();
        ts.truncate(0,1);
        assertEquals(1, ts.getStack().length);
        assertNotContainsStack(ts.getStack(), new StackTraceElement(this.getClass().getName(), "testTruncate", "ThreadStackTest.java", 23), 2);
        assertContainsStack(getStack().getStack(), new StackTraceElement(this.getClass().getName(), "getStack", "ThreadStackTest.java", 15), 2);
    }
}
