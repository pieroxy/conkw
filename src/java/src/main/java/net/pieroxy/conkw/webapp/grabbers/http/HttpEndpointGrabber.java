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
import java.util.logging.Level;
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
            collectDataFromHttp(begin, firstByte, lastByte, body, endPointMonitoringConfig, res);
        } catch (Exception e) {
            res.collect(AccumulatorUtils.addToMetricName("", endPointMonitoringConfig.id, "error"), e.getMessage());
        }
    }

    protected void collectDataFromHttp(long begin, long firstByte, long lastByte, String body, EndPointMonitoringConfig endPointMonitoringConfig, SimpleCollector res) {
        res.collect(AccumulatorUtils.addToMetricName(endPointMonitoringConfig.id, "size"), body.length());
        res.collect(AccumulatorUtils.addToMetricName(endPointMonitoringConfig.id, "firstByte", "time"), firstByte - begin);
        res.collect(AccumulatorUtils.addToMetricName(endPointMonitoringConfig.id, "lastByte", "time"), lastByte - begin);
        if (canLogFine()) log(Level.FINE, "Capturing Regexes: " + endPointMonitoringConfig.toExtract.size());
        for (EndPointMonitoringPatternConfig p : endPointMonitoringConfig.toExtract) {
            String rootMetricName = AccumulatorUtils.addToMetricName(endPointMonitoringConfig.id, p.getId());
            if (canLogFine()) log(Level.FINE, "Pattern: " + p.getPattern().pattern());
            Matcher m = p.getPattern().matcher(body);
            if (canLogFine()) log(Level.FINE, "Matches: " + m.matches()+ " Groups: " + m.groupCount());
            res.collect(AccumulatorUtils.addToMetricName(rootMetricName, "matched"), m.matches() ? 1 : 0);
            res.collect(AccumulatorUtils.addToMetricName(rootMetricName, "found"), getMatches(m));
            if (m.groupCount() == 1 && m.matches()) {
                String metricName = AccumulatorUtils.addToMetricName(rootMetricName, "captured");
                String values = m.group(1);
                if (canLogFine()) log(Level.FINE, "Captured: "+values);
                if (canLogFine()) log(Level.FINE, "With name: " + metricName);
                if (p.isNumber() !=null && p.isNumber()) {
                    res.collect(metricName, Double.parseDouble(values));
                } else {
                    res.collect(metricName, values);
                }
            }
        }
    }

    private double getMatches(Matcher m) {
        int res = 0;
        while(m.find()) res++;
        return res;
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
        List<EndPointMonitoringPatternConfig> toExtract;

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

        public List<EndPointMonitoringPatternConfig> getToExtract() {
            return toExtract;
        }

        public void setToExtract(List<EndPointMonitoringPatternConfig> toExtract) {
            this.toExtract = toExtract;
        }

        @Override
        public void addToHash(Md5Sum sum) {
            sum.add(id).add(url);
            if (toExtract!=null) {
                for (EndPointMonitoringPatternConfig c : toExtract) {
                    c.addToHash(sum);
                }
            }
        }
    }

    public static class EndPointMonitoringPatternConfig implements Hashable {
        private String id;
        private Pattern pattern;
        private Boolean number;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public Boolean isNumber() {
            return number;
        }

        public void setNumber(Boolean number) {
            this.number = number;
        }

        @Override
        public void addToHash(Md5Sum sum) {
            sum.add(id).add(pattern.pattern()).add(number ? "true" : "false");
        }
    }
}
