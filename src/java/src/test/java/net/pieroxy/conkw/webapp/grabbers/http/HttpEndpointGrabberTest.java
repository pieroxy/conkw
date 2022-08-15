package net.pieroxy.conkw.webapp.grabbers.http;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.webapp.model.ResponseData;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class HttpEndpointGrabberTest extends ConkwTestCase {
    public void testRegexCapturing() {
        HttpEndpointGrabber grabber = new HttpEndpointGrabber();
        grabber.setName("testg");

        SimpleCollector sc = new SimpleCollector(grabber, "");

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
        conf.getToExtract().add(new HttpEndpointGrabber.EndPointMonitoringPatternConfig());
        conf.getToExtract().get(2).setId("pat3");
        conf.getToExtract().get(2).setNumber(false);
        conf.getToExtract().get(2).setPattern(Pattern.compile(".*data(.*)toto"));
        conf.getToExtract().add(new HttpEndpointGrabber.EndPointMonitoringPatternConfig());
        conf.getToExtract().get(3).setId("pat4");
        conf.getToExtract().get(3).setNumber(true);
        conf.getToExtract().get(3).setPattern(Pattern.compile("\\d+"));

        grabber.collectDataFromHttp(0, 100, 200, "lots (2) of data123", conf, sc);
        sc.collectionDone();

        ResponseData data = sc.getDataCopy();
        assertMapContains(data.getNum(), "ex.firstByte.time", 100.);
        assertMapContains(data.getNum(), "ex.lastByte.time", 200.);

        assertMapContains(data.getNum(), "ex.pat1.matched", 1.);
        assertMapContains(data.getStr(), "ex.pat1.captured", "of");
        assertMapContains(data.getNum(), "ex.pat1.found", 0.);

        assertMapContains(data.getNum(), "ex.pat2.captured", 123.);
        assertMapContains(data.getNum(), "ex.pat2.matched", 1.);
        assertMapContains(data.getNum(), "ex.pat2.found", 0.);

        assertMapContains(data.getNum(), "ex.pat3.matched", 0.);
        assertMapContains(data.getNum(), "ex.pat3.found", 0.);
        assertMapDoesNotContain(data.getNum(), "ex.pat3.captured");
        assertMapDoesNotContain(data.getStr(), "ex.pat3.captured");

        assertMapContains(data.getNum(), "ex.pat4.matched", 0.);
        assertMapContains(data.getNum(), "ex.pat4.found", 2.);
        assertMapDoesNotContain(data.getNum(), "ex.pat4.captured");
        assertMapDoesNotContain(data.getStr(), "ex.pat4.captured");
    }
}
