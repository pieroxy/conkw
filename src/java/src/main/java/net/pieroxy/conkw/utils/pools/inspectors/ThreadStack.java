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
}
