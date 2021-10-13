package net.pieroxy.conkw.utils.pools.hashmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LightInstrumentedMap<K, V> implements Map<K, V> {
    private Map<K, V> instance = new HashMap<>();
    private int maxSize;
    private int maxItemsPredicted;

    public int getMaxSize() {
        return maxSize;
    }

    public int getMaxItemsPredicted() {
        return maxItemsPredicted;
    }

    /*
            Map implementation :: BEGIN
             */
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
        maxSize = Math.max(maxSize, instance.size()+1);
        return instance.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return instance.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        instance.putAll(m);
        maxSize = Math.max(maxSize, instance.size());
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
    /*
    Map implementation :: END
     */
}
