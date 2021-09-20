package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

public class SimpleTransientCollector extends AbstractSimpleCollector {
    public SimpleTransientCollector(Grabber g) {
        super(g);
    }

    @Override
    public synchronized void reset() {
        collected = new ResponseData(grabber, System.currentTimeMillis());
    }
}
