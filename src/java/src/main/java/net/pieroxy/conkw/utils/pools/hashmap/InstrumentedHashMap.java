package net.pieroxy.conkw.utils.pools.hashmap;

import net.pieroxy.conkw.utils.pools.ObservableObject;
import net.pieroxy.conkw.utils.pools.inspectors.ThreadStack;

import java.io.Closeable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InstrumentedHashMap<K, V> implements Map<K, V>, ObservableObject {

    private Map<K, V> instance;
    private boolean accessed = true;
    ThreadStack violation;

    public InstrumentedHashMap() {
        instance = new HashMap<>();
    }

    public InstrumentedHashMap(Map<K, V> instance) {
        this.instance = instance;
    }

    private void access() {
        if (accessed) return;
        accessed = true;
        violation = new ThreadStack(Thread.currentThread().getStackTrace());
    }

    // Map implementation below

    @Override
    public int size() {
        access();
        return instance.size();
    }

    @Override
    public boolean isEmpty() {
        access();
        return instance.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        access();
        return instance.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        access();
        return instance.containsValue(value);
    }

    @Override
    public V get(Object key) {
        access();
        return instance.get(key);
    }

    @Override
    public V put(K key, V value) {
        access();
        return instance.put(key, value);
    }

    @Override
    public V remove(Object key) {
        access();
        return instance.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        access();
        instance.putAll(m);
    }

    @Override
    public void clear() {
        access();
        instance.clear();
    }

    @Override
    public Set<K> keySet() {
        access();
        return instance.keySet();
    }

    @Override
    public Collection<V> values() {
        access();
        return instance.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        access();
        return instance.entrySet();
    }

    // End of Map implementation

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
        accessed = false;
    }

    @Override
    public ThreadStack getAccessedStack() {
        return violation;
    }
}
