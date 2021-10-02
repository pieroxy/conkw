package net.pieroxy.conkw.utils.pools;

import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.pools.inspectors.*;

public enum ObjectPoolBehavior {
    PROD((op)->new NoopInspector()),
    TRACK_NOT_DISPOSED((op)-> new UndisposedObjectsInspector()),
    TRACK_NOT_DISPOSED_LIVE((op)-> new RegularReportInspector(new UndisposedObjectsInspector(), CDuration.FIVE_SECONDS, op));

    private final InspectorProvider provider;

    ObjectPoolBehavior(InspectorProvider ip) {
        provider = ip;
    }

    public ObjectPoolInspector getInspector(ObjectPool op) {
        return provider.get(op);
    }
}
