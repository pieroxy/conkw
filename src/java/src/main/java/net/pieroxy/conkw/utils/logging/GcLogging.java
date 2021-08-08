package net.pieroxy.conkw.utils.logging;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GcLogging {
    private static final Logger LOGGER = Logger.getLogger("JVM GC");

    public static void installGCMonitoring() {
        List<GarbageCollectorMXBean> gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcbean : gcbeans) {
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = new NotificationListener() {
                @Override
                public void handleNotification(Notification notification, Object handback) {
                    if (LOGGER.isLoggable(Level.INFO)) {
                        if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                            GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                            int before = info.getGcInfo().getMemoryUsageBeforeGc().entrySet().stream().map(es -> es.getValue().getUsed() / 1024.).mapToInt(Double::intValue).sum();
                            int after = info.getGcInfo().getMemoryUsageAfterGc().entrySet().stream().map(es -> es.getValue().getUsed() / 1024.).mapToInt(Double::intValue).sum();
                            int allocated = info.getGcInfo().getMemoryUsageAfterGc().entrySet().stream().map(es -> es.getValue().getCommitted() / 1024.).mapToInt(Double::intValue).sum();
                            LOGGER.info("[GC (" + info.getGcCause() + ")  " + before + "K->" + after + "K(" + allocated + "K), " + info.getGcInfo().getDuration() + "ms]");
                        }
                    }
                }
            };

            //Add the listener
            emitter.addNotificationListener(listener, null, null);
        }
    }
}
