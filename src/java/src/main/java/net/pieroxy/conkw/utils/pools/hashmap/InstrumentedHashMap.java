package net.pieroxy.conkw.utils.pools.hashmap;

import net.pieroxy.conkw.utils.pools.Disposable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class InstrumentedHashMap<K, V> implements Map<K, V>, Disposable {

    private Map<K, V> instance;
    private boolean accessed;
    private boolean modified;



    public InstrumentedHashMap() {
        instance = HashMapPool.getInstance().getNew();
    }

    public InstrumentedHashMap(Map<K, V> instance) {
        this.instance = instance;
    }

    @Override
    public int size() {
        return instance.size();
    }

    @Override
    public boolean isEmpty() {
        return instance.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return instance.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return instance.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return instance.get(key);
    }

    @Override
    public V put(K key, V value) {
        return instance.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return instance.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        instance.putAll(m);
    }

    @Override
    public void clear() {
        instance.clear();
    }

    @Override
    public Set<K> keySet() {
        return instance.keySet();
    }

    @Override
    public Collection<V> values() {
        return instance.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return instance.entrySet();
    }

    @Override
    public void dispose() {
        Map<K, V> oi = instance;
        instance = null;
        HashMapPool.getInstance().dispose(oi);
    }

    public Map<K, V> getInstance() {
        return instance;
    }

    public void setInstance(Map<K, V> instance) {
        this.instance = instance;
    }
}
