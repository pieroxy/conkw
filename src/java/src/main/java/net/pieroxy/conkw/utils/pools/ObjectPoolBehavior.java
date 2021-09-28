package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.pools.inspectors.InspectorProvider;
import net.pieroxy.conkw.utils.pools.inspectors.NoopInspector;
import net.pieroxy.conkw.utils.pools.inspectors.ObjectPoolInspector;
import net.pieroxy.conkw.utils.pools.inspectors.UndisposedObjectsInspector;

public enum ObjectPoolBehavior {
    PROD(()->new NoopInspector()),
    TRACK_NOT_DISPOSED(()-> new UndisposedObjectsInspector());

    private final InspectorProvider provider;

    ObjectPoolBehavior(InspectorProvider ip) {
        provider = ip;
    }

    public ObjectPoolInspector getInspector() {
        return provider.get();
    }
}
