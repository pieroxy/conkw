package net.pieroxy.conkw.webapp.grabbers.http;

import net.pieroxy.conkw.accumulators.AccumulatorUtils;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.hashing.Hashable;
import net.pieroxy.conkw.utils.hashing.Md5Sum;

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
        HttpEndpointGrabberConfig res = new HttpEndpointGrabberConfig();
        res.setTtl(CDuration.ZERO);
        return res;
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
            long begin = System.nanoTime();
            URL u = new URL(endPointMonitoringConfig.url);
            HttpURLConnection http = (HttpURLConnection)u.openConnection();
            http.setConnectTimeout(500);
            http.setReadTimeout(500);
            http.setRequestMethod("GET"); // PUT is another valid option
            http.connect();
            int rc = http.getResponseCode();
            long firstByte = System.nanoTime();
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "status"), rc);
            InputStream is;
            if (rc >= 200 && rc<300) {
                is = http.getInputStream();
            } else {
                is = http.getErrorStream();
            }
            String body = DebugTools.isToString(is);
            long lastByte = System.nanoTime();
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "size"), body.length());
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "firstByteTime"), firstByte - begin);
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "lastByteTime"), lastByte - begin);
            for (Map.Entry<String, Pattern> p : endPointMonitoringConfig.toExtract.entrySet()) {
                Matcher m = p.getValue().matcher(body);
                if (m.groupCount() == 1) {
                    res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, p.getKey()), m.group(1));
                }
            }
        } catch (Exception e) {
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "error"), e.getMessage());
        }
    }

    @Override
    protected String getCacheKey() {
        Md5Sum sum = new Md5Sum();
        getConfig().addToHash(sum);
        return sum.getMd5Sum();
    }

    @Override
    protected void applyConfig(Map<String, String> config, Map<String, Map<String, String>> configs) {
        // This is the old way of doing configuration
    }
    
    public static class HttpEndpointGrabberConfig extends TimeThrottledGrabber.TimeThrottledGrabberConfig implements Hashable {
        List<EndPointMonitoringConfig> endpoints;

        @Override
        public String toString() {
            return "HttpEndpointGrabberConfig{" +
                    "endpoints=" + endpoints +
                    '}';
        }

        public List<EndPointMonitoringConfig> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(List<EndPointMonitoringConfig> endpoints) {
            this.endpoints = endpoints;
        }

        @Override
        public void addToHash(Md5Sum sum) {
            if (endpoints == null) return;
            for (EndPointMonitoringConfig c : endpoints) {
                c.addToHash(sum);
            }
        }
    }

    public static class EndPointMonitoringConfig implements Hashable {
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, Pattern> getToExtract() {
            return toExtract;
        }

        public void setToExtract(Map<String, Pattern> toExtract) {
            this.toExtract = toExtract;
        }

        @Override
        public void addToHash(Md5Sum sum) {
            sum.add(id).add(url);
            for (Map.Entry<String, Pattern> e : toExtract.entrySet()) {
                sum.add(e.getKey()).add(e.getValue().pattern());
            }
        }
    }
}
