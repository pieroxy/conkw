package net.pieroxy.conkw.utils;

public class TimedData<T> {
    private long lastUsed;
    private T data;

    public TimedData(T data) {
        this.data = data;
    }

    public long getAge() {
        return System.currentTimeMillis() - lastUsed;
    }

    public void use() {
        lastUsed = System.currentTimeMillis();
    }

    public T getData() {
        return data;
    }
}
