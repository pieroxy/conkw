package net.pieroxy.conkw.utils;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PrefixedKeyMap<V> implements Map<String,V> {
    private final Map<String,V> src;
    private String prefix = "";
    private int size = -1;

    public PrefixedKeyMap(Map<String, V> originalData) {
        this.src = originalData;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        this.size = -1;
    }


    @Override
    public int size() {
        if (size == -1) {
            if (StringUtil.isNullOrEmptyTrimmed(prefix)) {
                size = src.size();
            } else {
                size = (int)src.keySet().stream().filter(s -> s.startsWith(prefix)).count();
            }
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return src.containsKey(prefix+key);
    }

    @Override
    public boolean containsValue(Object value) {
        return src.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return src.get(prefix+key);
    }

    @Override
    public V put(String key, V value) {
        throw new NotImplementedException();
    }

    @Override
    public V remove(Object key) {
        throw new NotImplementedException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> m) {
        throw new NotImplementedException();
    }

    @Override
    public void clear() {
        throw new NotImplementedException();
    }

    @Override
    public Set<String> keySet() {
        return src.keySet().stream().filter(s -> s.startsWith(prefix)).map(s -> s.substring(prefix.length())).collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        return src.keySet().stream().filter(s -> s.startsWith(prefix)).map(s -> src.get(s)).collect(Collectors.toSet());
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        throw new NotImplementedException();
    }
}
