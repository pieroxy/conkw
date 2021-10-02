package net.pieroxy.conkw.utils.pools.hashmap;

import net.pieroxy.conkw.utils.pools.ObservableObject;

import java.io.Closeable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InstrumentedHashMap<K, V> implements Map<K, V>, ObservableObject {

    private Map<K, V> instance;
    private boolean accessed;

    public InstrumentedHashMap() {
        instance = new HashMap<>();
    }

    public InstrumentedHashMap(Map<K, V> instance) {
        this.instance = instance;
    }

    @Override
    public int size() {
        accessed = true;
        return instance.size();
    }

    @Override
    public boolean isEmpty() {
        accessed = true;
        return instance.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        accessed = true;
        return instance.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        accessed = true;
        return instance.containsValue(value);
    }

    @Override
    public V get(Object key) {
        accessed = true;
        return instance.get(key);
    }

    @Override
    public V put(K key, V value) {
        accessed = true;
        return instance.put(key, value);
    }

    @Override
    public V remove(Object key) {
        accessed = true;
        return instance.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        accessed = true;
        instance.putAll(m);
    }

    @Override
    public void clear() {
        accessed = true;
        instance.clear();
    }

    @Override
    public Set<K> keySet() {
        accessed = true;
        return instance.keySet();
    }

    @Override
    public Collection<V> values() {
        accessed = true;
        return instance.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        accessed = true;
        return instance.entrySet();
    }

    public Map<K, V> getInstance() {
        return instance;
    }

    public void setInstance(Map<K, V> instance) {
        this.instance = instance;
    }

    @Override
    public boolean hasBeenAccessed() {
        return accessed;
    }

    @Override
    public void resetAccessedValue() {

    }
}
