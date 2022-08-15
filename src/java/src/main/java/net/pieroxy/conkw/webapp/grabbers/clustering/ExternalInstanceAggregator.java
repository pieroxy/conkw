package net.pieroxy.conkw.webapp.grabbers.clustering;

import net.pieroxy.conkw.accumulators.Accumulator;
import net.pieroxy.conkw.accumulators.AccumulatorCollector;
import net.pieroxy.conkw.accumulators.implementations.RootAccumulator;
import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.config.Credentials;
import net.pieroxy.conkw.config.CredentialsProvider;
import net.pieroxy.conkw.grabbersBase.AsyncGrabber;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.utils.ApiAuthenticator;
import net.pieroxy.conkw.utils.DebugTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;
import net.pieroxy.conkw.utils.prefixeddata.PrefixedDataRecordImpl;
import net.pieroxy.conkw.webapp.model.MetricsApiResponse;
import net.pieroxy.conkw.webapp.servlets.ApiAuthManager;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;

public class ExternalInstanceAggregator<T extends DataRecord> extends AsyncGrabber<AccumulatorCollector<T>, ExternalInstanceAggregator.ExternalInstanceAggregatorConfig> {
    private String sessionToken;
    private boolean destroyed = false;

    @Override
    public boolean changed(AccumulatorCollector<T> c) {
        return true;
    }

    @Override
    public ExternalInstanceAggregatorConfig getDefaultConfig() {
        return new ExternalInstanceAggregatorConfig();
    }

    @Override
    public void grabSync(AccumulatorCollector<T> c) {
        long begin = System.currentTimeMillis();
        Thread[]threads = new Thread[getConfig().getEndpoints().size()];
        for (int i=0 ; i< threads.length ; i++) {
            final int index = i;
            threads[i] = new Thread(
                    () -> {
                        String error = grabOne(c.getAccumulator(), (ExternalInstanceAggregatorConfigEndpoint) getConfig().getEndpoints().get(index));
                        if (error!=null) {
                            synchronized (c) { c.addError(error); }
                        }
                    },
                    getGrabberFQN() + " fetcher " + index
            );
            threads[i].setDaemon(true);
            threads[i].start();
        }
        for (int i=0 ; i< threads.length ; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                log(Level.WARNING, "Thread was interrupted ... ", e);
            }
        }
        log(Level.INFO, "Time to grab " + (System.currentTimeMillis() - begin));
        c.setTime(System.currentTimeMillis()-begin);
        c.setTimestamp(System.currentTimeMillis());
    }
    public String grabOne(Accumulator<T> acc2, ExternalInstanceAggregatorConfigEndpoint endpoint) {
        URL url = null;
        try {
            url = new URL(endpoint.getUrl() + "?grabbers=" + endpoint.getGrabberName() + (StringUtil.isNullOrEmptyTrimmed(sessionToken)?"":("&"+ ApiAuthManager.SID_FIELD+"="+sessionToken)));
            if (canLogFine()) {
                log(Level.FINE, "About to fetch data from " + url);
            }
            long begin = System.currentTimeMillis();
            URLConnection con = url.openConnection();
            con.setConnectTimeout(500);
            con.setReadTimeout(500);
            HttpURLConnection http = (HttpURLConnection) con;
            http.connect();

            InputStream is;
            if (canLogFine()) {
                is = DebugTools.debugHttpRequest(http);
            } else {
                is = http.getInputStream();
            }

            try (MetricsApiResponse data = JsonHelper.getJson().deserialize(MetricsApiResponse.class, is)) {
                if (canLogFine()) {
                    log(Level.FINE, "Time to grab "+endpoint+": " + (System.currentTimeMillis()-begin) + "ms");
                }
                if (data.isNeedsAuthentication()) {
                    try {
                        sessionToken = ApiAuthenticator.authenticate(getCredentials(getConfig()), endpoint.getUrl(), this);
                    } catch (DisplayMessageException e) {
                        if (destroyed) return null;
                        return "H2"+e.getMessage();
                    }
                }
                Accumulator<T> newacc = acc2.getFreshInstance();
                PrefixedDataRecordImpl datalogged = new PrefixedDataRecordImpl(data.getMetrics().get(endpoint.getGrabberName()));
                newacc.initializeFromData(datalogged);
                newacc.prepareNewSession();
                //System.out.println("Initialized from " + endpoint + " " + newacc.getTotal() + " EL=" + datalogged.getValues().get(RootAccumulator.ELAPSED) + " RM=" + datalogged.getValues().get("timehist.2.histValue"));
                synchronized (acc2) {
                    acc2.sumWith(newacc);
                }
                //System.err.println("newacc: " + ((Simple125Histogram)((NamedAccumulator)((MultiAccumulator)((RootAccumulator)newacc).getAccumulator()).getAccumulator(0)).getAccumulator()).getValues()[1]);
                //System.err.println("acc2: " + ((Simple125Histogram)((NamedAccumulator)((MultiAccumulator)((RootAccumulator)acc2).getAccumulator()).getAccumulator(0)).getAccumulator()).getValues()[1]);
            }
        } catch (Exception e) {
            if (destroyed) return null;
            log(Level.SEVERE, "EIG Grabbing " + getName() + " with URL " + url, e);
            return "H3"+e.getMessage();
        }
        return null;
    }

    @Override
    public AccumulatorCollector<T> parseCollector(String param) {
        // There is a cache, so there is probably no need to optimize this.
        RootAccumulator res = new AccumulatorExpressionParser().parse(param);
        return new AccumulatorCollector<>(this, param, "", res);
    }

    @Override
    public String getDefaultName() {
        return null;
    }

    @Override
    protected void disposeSync() {
        destroyed = true;
        super.disposeSync();

    }

    @Override
    public AccumulatorCollector<T> getDefaultCollector() {
        return new AccumulatorCollector<T>(this, DEFAULT_CONFIG_KEY, "default", getDefaultAccumulator());
    }

    public static class ExternalInstanceAggregatorConfig<T extends DataRecord> implements CredentialsProvider {
        private Credentials credentials;
        private String credentialsRef;
        private List<ExternalInstanceAggregatorConfigEndpoint> endpoints;


        @Override
        public Credentials getCredentials() {
            return credentials;
        }

        @Override
        public String getCredentialsRef() {
            return credentialsRef;
        }

        public void setCredentials(Credentials credentials) {
            this.credentials = credentials;
        }

        public void setCredentialsRef(String credentialsRef) {
            this.credentialsRef = credentialsRef;
        }

        public List<ExternalInstanceAggregatorConfigEndpoint> getEndpoints() {
            return endpoints;
        }

        public void setEndpoints(List<ExternalInstanceAggregatorConfigEndpoint> endpoints) {
            this.endpoints = endpoints;
        }
    }

    public static class ExternalInstanceAggregatorConfigEndpoint {
        private String url;
        private String grabberName;

        public String getUrl() {
            return url;
        }

        public String getGrabberName() {
            return grabberName;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setGrabberName(String grabberName) {
            this.grabberName = grabberName;
        }

        @Override
        public String toString() {
            return "ExternalInstanceAggregatorConfigEndpoint{" +
                    "url='" + url + '\'' +
                    ", grabberName='" + grabberName + '\'' +
                    '}';
        }
    }
}
