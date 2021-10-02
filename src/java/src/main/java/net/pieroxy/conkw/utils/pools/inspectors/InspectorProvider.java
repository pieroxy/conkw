package net.pieroxy.conkw.utils.pools.inspectors;

import net.pieroxy.conkw.utils.pools.ObjectPool;

public interface InspectorProvider {
    ObjectPoolInspector get(ObjectPool op);
}
