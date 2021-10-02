package net.pieroxy.conkw.utils.pools.inspectors;

import java.util.Arrays;

public class ThreadStack {
    private StackTraceElement[] stack;

    public ThreadStack(StackTraceElement[]stack) {
        this.stack = stack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThreadStack that = (ThreadStack) o;
        return Arrays.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(stack);
    }

    public void truncate(int depth) {
        if (stack.length > depth) {
            StackTraceElement[] truncated = new StackTraceElement[depth];
            System.arraycopy(stack, stack.length-depth, truncated, 0, depth);
            stack = truncated;
        }
    }

    public StackTraceElement[] getStack() {
        return stack;
    }
}
