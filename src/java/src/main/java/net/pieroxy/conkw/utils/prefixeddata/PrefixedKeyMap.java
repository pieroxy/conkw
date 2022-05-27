package net.pieroxy.conkw.utils.prefixeddata;

import net.pieroxy.conkw.pub.misc.ConkwCloseable;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class PrefixedKeyMap<V> implements Map<String,V>, ConkwCloseable {
    private final Map<String,V> src;
    private String prefix = "";
    private Stack<String> prefixes = new Stack<>();
    private int size = -1;

    public PrefixedKeyMap(Map<String, V> originalData) {
        this.src = originalData;
    }

    public void pushPrefix(String prefix) {
        prefixes.push(prefix);
        prefixChanged();
    }

    public String popPrefix() {
        String res = prefixes.pop();
        prefixChanged();
        return res;
    }

    private void prefixChanged() {
        this.prefix = prefixes.stream().collect(Collectors.joining());
        this.size = -1;
    }

    public String getCurrentPrefix() {
        return prefix;
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
    public Set<Map.Entry<String, V>> entrySet() {
        return src.entrySet().stream().filter(e -> e.getKey().startsWith(prefix)).map(e -> new Entry(e.getKey().substring(prefix.length()), e.getValue())).collect(Collectors.toSet());
    }

    @Override
    public void close() {
        HashMapPool.getInstance().giveBack(src);
    }

    private class Entry implements Map.Entry<String, V> {
        private String key;
        private V value;

        public Entry(String key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}
