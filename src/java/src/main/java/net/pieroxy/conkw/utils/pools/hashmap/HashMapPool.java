package net.pieroxy.conkw.utils.pools.hashmap;

import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.pools.ObjectPool;
import net.pieroxy.conkw.utils.pools.inspectors.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class HashMapPool extends ObjectPool<Map, Integer> {
    private final static Logger LOGGER = Logger.getLogger(ObjectPool.class.getName());

    public static final int SIZE_UNKNOWN = -1;

    // For easy use when needed:
    //static HashMapPool instance = new HashMapPool((op) -> new RegularReportInspector(new CountProducersInspector(6), CDuration.FIVE_SECONDS, op));
    //static HashMapPool instance = new HashMapPool((op) -> new RegularReportInspector(new ReusedRecycledObjectInspector(), CDuration.FIVE_SECONDS, op));
    //static HashMapPool instance = new HashMapPool((op) -> new RegularReportInspector(new UndisposedObjectsInspector(), CDuration.FIVE_SECONDS, op));
    static HashMapPool instance = new HashMapPool((op) -> new NoopInspector());

    public HashMapPool(InspectorProvider inspector) {
        super(inspector);
    }

    public final static HashMapPool getInstance() {
        return instance;
    }

    @Override
    protected Map createNewInstance() {
        return new LightInstrumentedMap();
    }

    @Override
    protected Map getInstanceToRecycle(Map instance) {
        if (instance!=null && instance instanceof LightInstrumentedMap) {
            instance.clear();
            return instance;
        }
        return null;
    }

    @Override
    protected Map prepareInstance(Integer param, Map result) {
        ((LightInstrumentedMap)result).setMaxItemsPredicted(param);
        if (!result.isEmpty()) {
            LOGGER.log(Level.SEVERE, "A map was accessed after being given back to the pool. You should run HashMapPool with the ReusedRecycledObjectInspector to figure out the code responsible. Bad things will happen.");
        }
        return result;
    }

    public <K, V> Map<K, V> borrow(Map<K, V> database, int additionalItems) {
        Map<K, V> map = (Map<K, V>) borrow(database.size() + additionalItems);
        map.putAll(database);
        return map;
    }

    @Override
    public String getDebugString() {
        StringBuilder sb = new StringBuilder(super.getDebugString()).append("\n[");
        Iterator<Map> it = getPoolObjects();
        List<Integer> sizes = new ArrayList<>();
        while (it.hasNext()) {
            sizes.add(((LightInstrumentedMap)it.next()).getMaxSize());
        }
        Collections.sort(sizes);
        sb.append(sizes.stream().map(String::valueOf).collect(Collectors.joining(",")));
        sb.append("]");
        return sb.toString();
    }

    public static int getUniqueCode(String...strings) {
        int res = 0;
        for (String s : strings) if (s!=null) res ^= s.hashCode();
        if (res > 0) res = -res;
        if (res > -100) res -= 100;
        return res;
    }
}
