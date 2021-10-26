package net.pieroxy.conkw.utils.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is a write only implementation of java.util.Map
 * @param <K>
 * @param <V>
 */
public class BlackHoleMap<K,V> implements Map<K, V> {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
    }

    @Override
    public void clear() {
    }

    @Override
    public Set<K> keySet() {
        return BlackHoleSet.getInstance();
    }

    @Override
    public Collection<V> values() {
        return BlackHoleSet.getInstance();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return BlackHoleSet.getInstance();
    }
}
