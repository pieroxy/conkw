package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.utils.JsonHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpLogger  {
    private final static Logger LOGGER = Logger.getLogger(HttpLogger.class.getName());
    private static final Logger HTTP_LOGGER = Logger.getLogger("http_log");

    public static void log(String host, String uri, String userAgent, String verb, long time, int status, int size, String contentType, String ip) {
        if (HTTP_LOGGER.isLoggable(Level.FINE)) {
            String logData = JsonHelper.toString(new HttpLogEvent(host, uri, userAgent, verb, (int)time, status, size, contentType, ip));
            HTTP_LOGGER.fine(logData);
        }
    }
}


