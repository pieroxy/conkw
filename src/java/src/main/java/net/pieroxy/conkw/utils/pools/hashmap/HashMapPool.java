package net.pieroxy.conkw.utils.pools.hashmap;

import net.pieroxy.conkw.utils.pools.ObjectPool;
import net.pieroxy.conkw.utils.pools.ObjectPoolBehavior;

import java.util.HashMap;
import java.util.Map;

public class HashMapPool extends ObjectPool<Map> {
    static HashMapPool instance = new HashMapPool(ObjectPoolBehavior.TRACK_NOT_DISPOSED_LIVE);

    public HashMapPool(ObjectPoolBehavior behavior) {
        super(behavior);
    }

    public final static ObjectPool<Map> getInstance() {
        return instance;
    }

    @Override
    protected Map createNewInstance() {
        return new HashMap();
    }

    @Override
    protected Map getInstanceToRecycle(Map instance) {
        return (instance!=null && (instance instanceof HashMap || instance instanceof InstrumentedHashMap)) ? instance : null;
    }
}
