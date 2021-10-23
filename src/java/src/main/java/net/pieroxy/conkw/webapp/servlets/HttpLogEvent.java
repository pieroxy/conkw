package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.pub.mdlog.LogRecord;

import com.dslplatform.json.CompiledJson;

import java.util.HashMap;
import java.util.Map;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class HttpLogEvent implements LogRecord {
    public static final String NAME="event_http";
    public static final String SIZE="size";
    public static final String TIME="time";
    public static final String CONTENT_TYPE="contentType";
    public static final String HOST="host";
    public static final String URI="uri";
    public static final String STATUS="status";
    public static final String VERB="verb";

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

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public Map<String, String> getDimensions() {
        Map<String, String> dimensions = new HashMap<>();
        dimensions.put(CONTENT_TYPE, getContentType());
        dimensions.put(HOST, getHost());
        dimensions.put(URI, getFirstDepth(getUri()));
        dimensions.put(STATUS, String.valueOf(getStatus()));
        dimensions.put(VERB, getVerb());
        return dimensions;
    }

    private String getFirstDepth(String host) {
        if (host == null) return "";
        int pos1 = host.indexOf('/');
        if (pos1<0) return host;
        int pos2 = host.indexOf('/', pos1+1);
        if (pos2<0) return host;
        return host.substring(0, pos2);
    }

    @Override
    public Map<String, Double> getValues() {
        Map<String, Double> values= new HashMap<>();
        values.put(LogRecord.COUNT, 1d);
        values.put(SIZE, (double)size);
        values.put(TIME, (double)time);
        return values;
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
