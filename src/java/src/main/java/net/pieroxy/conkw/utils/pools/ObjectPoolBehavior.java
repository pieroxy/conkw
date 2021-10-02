package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.pools.inspectors.*;

public enum ObjectPoolBehavior {
    PROD(()->new NoopInspector()),
    TRACK_NOT_DISPOSED(()-> new UndisposedObjectsInspector()),
    TRACK_NOT_DISPOSED_LIVE(()-> new RegularReportInspector(new UndisposedObjectsInspector(), CDuration.ONE_MINUTE));

    private final InspectorProvider provider;

    ObjectPoolBehavior(InspectorProvider ip) {
        provider = ip;
    }

    public ObjectPoolInspector getInspector() {
        return provider.get();
    }
}
