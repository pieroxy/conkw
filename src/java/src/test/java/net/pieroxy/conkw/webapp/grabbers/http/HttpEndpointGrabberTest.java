package net.pieroxy.conkw.webapp.grabbers.http;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.collectors.SimpleTransientCollector;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class HttpEndpointGrabberTest extends ConkwTestCase {
    public void testRegexCapturing() {
        HttpEndpointGrabber grabber = new HttpEndpointGrabber();
        grabber.setName("testg");

        SimpleCollector sc = new SimpleTransientCollector(grabber, "");

        HttpEndpointGrabber.EndPointMonitoringConfig conf = new HttpEndpointGrabber.EndPointMonitoringConfig();
        conf.setId("ex");
        conf.setToExtract(new ArrayList<>());
        conf.getToExtract().add(new HttpEndpointGrabber.EndPointMonitoringPatternConfig());
        conf.getToExtract().get(0).setId("pat1");
        conf.getToExtract().get(0).setNumber(false);
        conf.getToExtract().get(0).setPattern(Pattern.compile(".* (.*) .*"));
        conf.getToExtract().add(new HttpEndpointGrabber.EndPointMonitoringPatternConfig());
        conf.getToExtract().get(1).setId("pat2");
        conf.getToExtract().get(1).setNumber(true);
        conf.getToExtract().get(1).setPattern(Pattern.compile(".*data(.*)"));

        grabber.collectDataFromHttp(0, 100, 200, "lots of data123", conf, sc);
        sc.collectionDone();

        ResponseData data = sc.getDataCopy();
        assertMapContains(data.getNum(), "ex.firstByte.time", 100.);
        assertMapContains(data.getNum(), "ex.lastByte.time", 200.);
        assertMapContains(data.getStr(), "ex.pat1", "of");
        assertMapContains(data.getNum(), "ex.pat2", 123.);
    }
}
