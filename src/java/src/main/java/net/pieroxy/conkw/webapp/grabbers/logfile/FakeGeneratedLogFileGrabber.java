package net.pieroxy.conkw.webapp.grabbers.logfile;

import net.pieroxy.conkw.accumulators.AccumulatorCollector;
import net.pieroxy.conkw.accumulators.implementations.RootAccumulator;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.HttpLogEvent;

import java.util.List;

public class FakeGeneratedLogFileGrabber extends AsyncGrabber<AccumulatorCollector<ResponseData>, FakeGeneratedLogFileGrabber.FakeGeneratedLogFileGrabberConfig> {

    @Override
    public boolean changed(AccumulatorCollector<ResponseData> c) {
        return true;
    }

    @Override
    public FakeGeneratedLogFileGrabberConfig getDefaultConfig() {
        return new FakeGeneratedLogFileGrabberConfig();
    }

    @Override
    public void grabSync(AccumulatorCollector<ResponseData> c) {
        getConfig().getTraffic().forEach(traffic -> {
            int qps = (int)addVariation(traffic.qps);
            for (int i=0 ; i<qps ; i++) {
                ResponseData rd = new ResponseData(this, System.currentTimeMillis(), 5, 15);
                rd.getNum().put(HttpLogEvent.SIZE, addVariation(traffic.size));
                rd.getNum().put(HttpLogEvent.TIME, addVariation(traffic.time));
                rd.getStr().put(HttpLogEvent.CONTENT_TYPE, traffic.contentType);
                rd.getStr().put(HttpLogEvent.HOST, traffic.host);
                rd.getStr().put(HttpLogEvent.STATUS, traffic.status + "");
                rd.getStr().put(HttpLogEvent.URI, traffic.uri);
                rd.getStr().put(HttpLogEvent.VERB, traffic.verb);
                rd.getStr().put("server", traffic.server);
                c.getAccumulator().add(rd);
            }
        });
    }

    double addVariation(int num) {
        return Math.round(num * (Math.random()+0.5));
    }

    @Override
    public String getDefaultName() {
        return "fakehttp";
    }

    @Override
    public AccumulatorCollector<ResponseData> getDefaultCollector(boolean dontcare) {
        return new AccumulatorCollector<>(this, DEFAULT_CONFIG_KEY, "default", new RootAccumulator<>("default",getDefaultAccumulator()));
    }

    public static class FakeGeneratedLogFileGrabberConfig {
        private List<FakeGeneratedLogFileGrabberConfigRequest> traffic;

        public FakeGeneratedLogFileGrabberConfig() {
        }

        public List<FakeGeneratedLogFileGrabberConfigRequest> getTraffic() {
            return traffic;
        }

        public void setTraffic(List<FakeGeneratedLogFileGrabberConfigRequest> traffic) {
            this.traffic = traffic;
        }
    }

    public static class FakeGeneratedLogFileGrabberConfigRequest {
        private int qps;
        private String server;
        private String host;
        private String uri;
        private String userAgent;
        private String verb;
        private int time;
        private int status;
        private int size;
        private String contentType;
        private String ip;

        public FakeGeneratedLogFileGrabberConfigRequest() {
        }

        public int getQps() {
            return qps;
        }

        public void setQps(int qps) {
            this.qps = qps;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
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
