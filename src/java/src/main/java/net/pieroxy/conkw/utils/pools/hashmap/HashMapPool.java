package net.pieroxy.conkw.utils.pools.hashmap;

import net.pieroxy.conkw.utils.pools.ObjectPool;

import java.util.HashMap;
import java.util.Map;

public class HashMapPool extends ObjectPool<Map> {
    static HashMapPool instance = new HashMapPool();

    public final static ObjectPool<Map> getInstance() {
        return instance;
    }

    @Override
    protected Map createNewInstance() {
        return new HashMap();
    }

    @Override
    protected Map getInstanceToRecycle(Map instance) {
        return (instance!=null && instance instanceof HashMap) ? instance : null;
    }
}
