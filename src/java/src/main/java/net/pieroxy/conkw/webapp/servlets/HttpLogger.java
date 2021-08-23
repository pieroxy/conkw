package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.CompiledJson;
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

    @CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
    public static class HttpLogEvent {
        public static final String NAME="event_http";

        private String host;
        private String uri;
        private String userAgent;
        private String verb;
        private int time;
        private int status;
        private int size;
        private String contentType;
        private String ip;

        public HttpLogEvent(String host, String uri, String userAgent, String verb, int time, int status, int size, String contentType, String ip) {
            this.host = host;
            this.uri = uri;
            this.userAgent = userAgent;
            this.verb = sanitizedVerb(verb);
            this.time = time;
            this.status = status;
            this.size = size;
            this.contentType = sanitizedContentType(contentType);
            this.ip = ip;
        }

        public String getName() {
            return NAME;
        }

        public String sanitizedContentType(String ct) {
            if (getStatus() <  200 || getStatus() >  299) return null;
            if (ct == null) {
                System.out.println("Empty CT for " + uri);
                return null;
            }
            if (ct.contains(";")) {
                ct = ct.substring(0, ct.indexOf(';'));
            }
            return ct;
        }

        public String sanitizedVerb(String res) {
            if (res==null) return null;
            if (res.length()==0) return null;
            return res;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getVerb() {
            return verb;
        }

        public void setVerb(String verb) {
            this.verb = verb;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }
    }

}


