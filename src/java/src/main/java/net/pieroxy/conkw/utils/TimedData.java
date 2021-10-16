package net.pieroxy.conkw.utils;

public class TimedData<T> implements ConkwCloseable {
    private long lastUsed;
    private T data;

    public TimedData(T data) {
        this.data = data;
    }

    public long getAge() {
        return System.currentTimeMillis() - lastUsed;
    }

    public T getData() {
        return data;
    }

    public void useNow() {
        lastUsed = System.currentTimeMillis();
    }

    @Override
    public void close() {
        if (data instanceof ConkwCloseable) ((ConkwCloseable) data).close();
    }
}