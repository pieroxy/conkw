package net.pieroxy.conkw.collectors;

import net.pieroxy.conkw.grabbersBase.Grabber;
import net.pieroxy.conkw.webapp.grabbers.procgrabber.ProcGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;

public class SimpleTransientCollector extends AbstractSimpleCollector {
    public SimpleTransientCollector(Grabber g, String configKey) {
        super(g, configKey);
    }

    @Override
    public synchronized void prepareForCollection() {
        completedCollection = collectionInProgress;
        collectionInProgress = new ResponseData(grabber, System.currentTimeMillis());
    }

    @Override
    public void collectionDone() {
        completedCollection = collectionInProgress;
    }
}
