package net.pieroxy.conkw.webapp.grabbers.http;

import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.duration.CDuration;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpEndpointGrabber extends TimeThrottledGrabber<HttpEndpointGrabber.HttpEndpointGrabberConfig> {

    @Override
    public String getDefaultName() {
        return "httpEndpoint";
    }

    @Override
    public HttpEndpointGrabberConfig getDefaultConfig() {
        return new HttpEndpointGrabberConfig();
    }

    @Override
    protected CDuration getDefaultTtl() {
        return null;
    }

    @Override
    protected void load(SimpleCollector res) {
        getConfig().endpoints.forEach((e) -> this.loadEndpoint(res, e));
    }

    private void loadEndpoint(SimpleCollector res, EndPointMonitoringConfig endPointMonitoringConfig) {
        try {
            URL u = new URL(endPointMonitoringConfig.url);
            HttpURLConnection http = (HttpURLConnection)u.openConnection();
            http.setRequestMethod("GET"); // PUT is another valid option
            http.connect();
            int rc = http.getResponseCode();
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "status"), rc);
            InputStream is;
            if (rc >= 200 && rc<300) {
                is = http.getInputStream();
            } else {
                is = http.getErrorStream();
            }
            String body = DebugTools.isToString(is);
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "size"), body.length());
            for (Map.Entry<String, Pattern> p : endPointMonitoringConfig.toExtract.entrySet()) {
                Matcher m = p.getValue().matcher(body);
                if (m.groupCount() == 1) {
                    res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, p.getKey()), body.length());
                }
            }
        } catch (Exception e) {
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "error"), e.getMessage());
        }
    }

    @Override
    protected String getCacheKey() {
        return null;
    }

    @Override
    protected void applyConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {

    }
    
    static class HttpEndpointGrabberConfig extends TimeThrottledGrabber.TimeThrottledGrabberConfig {
        List<EndPointMonitoringConfig> endpoints;

        @Override
        public String toString() {
            return "HttpEndpointGrabberConfig{" +
                    "endpoints=" + endpoints +
                    '}';
        }
    }

    static class EndPointMonitoringConfig {
        String id;
        String url;
        Map<String, Pattern> toExtract;

        @Override
        public String toString() {
            return "EndPointMonitoringConfig{" +
                    "id='" + id + '\'' +
                    ", url='" + url + '\'' +
                    ", toExtract=" + toExtract +
                    '}';
        }
    }
}
