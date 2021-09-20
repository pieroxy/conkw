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

    }

    public T getData() {
        return data;
    }

    public void useNow() {
        lastUsed = System.currentTimeMillis();
    }
}