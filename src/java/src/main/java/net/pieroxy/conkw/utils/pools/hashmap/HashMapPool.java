package net.pieroxy.conkw.utils.pools.hashmap;

import net.pieroxy.conkw.utils.pools.ObjectPool;
import net.pieroxy.conkw.utils.pools.inspectors.*;

import java.util.HashMap;
import java.util.Map;


public class HashMapPool extends ObjectPool<Map> {
    static HashMapPool instance = new HashMapPool((op) -> new NoopInspector());

    public HashMapPool(InspectorProvider inspector) {
        super(inspector);
    }

    public final static HashMapPool getInstance() {
        return instance;
    }

    @Override
    protected Map createNewInstance() {
        return new HashMap();
    }

    @Override
    protected Map getInstanceToRecycle(Map instance) {
        if (instance!=null && (instance instanceof HashMap || instance instanceof InstrumentedHashMap)) {
            instance.clear();
            return instance;
        }
        return null;
    }

    public <K, V> Map<K, V> borrow(Map<K, V> database) {
        Map<K, V> map = (Map<K, V>) borrow();
        map.putAll(database);
        return map;
    }
}
