package net.pieroxy.conkw.utils.pools.hashmap;

import net.pieroxy.conkw.utils.pools.ObservableObject;
import net.pieroxy.conkw.utils.pools.inspectors.ThreadStack;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class InstrumentedHashMap<K, V> extends LightInstrumentedMap<K, V> implements ObservableObject {
    private boolean accessed = true;
    ThreadStack violation;

    private void access() {
        if (accessed) return;
        accessed = true;
        violation = new ThreadStack(Thread.currentThread().getStackTrace());
    }

    // Map implementation below

    @Override
    public int size() {
        access();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        access();
        return super.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        access();
        return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        access();
        return super.containsValue(value);
    }

    @Override
    public V get(Object key) {
        access();
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        access();
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        access();
        return super.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        access();
        super.putAll(m);
    }

    @Override
    public void clear() {
        access();
        super.clear();
    }

    @Override
    public Set<K> keySet() {
        access();
        return super.keySet();
    }

    @Override
    public Collection<V> values() {
        access();
        return super.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        access();
        return super.entrySet();
    }

    // End of Map implementation

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
