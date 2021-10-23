package net.pieroxy.conkw.webapp.grabbers.logfile.parsers;

import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.pub.mdlog.LogParser;
import net.pieroxy.conkw.webapp.servlets.HttpLogEvent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpLogFileParser implements LogParser<HttpLogEvent> {
    private final static Logger LOGGER = Logger.getLogger(HttpLogFileParser.class.getName());
    // Sample line:
    // 2021-08-25 07:11:19    FINE                       http_log {"host":"localhost:12789","uri":"/emi","userAgent":"Java/1.8.0_181","verb":"emi","time":0,"status":200,"size":0,"contentType":"text/html","ip":"127.0.0.1"}

    @Override
    public HttpLogEvent parse(String line) {
        byte[]bytes = line.substring(59).getBytes(StandardCharsets.UTF_8);
        try {
            HttpLogEvent data = JsonHelper.getJson().deserialize(HttpLogEvent.class, bytes, bytes.length);
            return data;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "", e);
        }
        return null;
    }
}
