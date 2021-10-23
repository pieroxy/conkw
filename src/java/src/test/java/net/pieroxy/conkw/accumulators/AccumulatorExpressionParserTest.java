package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.accumulators.implementations.*;
import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.accumulators.parser.ParseException;
import net.pieroxy.conkw.pub.mdlog.LogRecord;
import net.pieroxy.conkw.pub.mdlog.GenericLogRecord;

import java.util.HashMap;
import java.util.Map;

public class AccumulatorExpressionParserTest extends ConkwTestCase {
    public void testSum() {
        Accumulator a = new AccumulatorExpressionParser().parse("sum(aa,1000)");
        assertNotNull(a);
        assertEquals(SumAccumulator.class, a.getClass());
        SumAccumulator sa = (SumAccumulator) a;
        assertEquals("aa", sa.getValueName());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        a.add(lr);
        Map<String, Double> result = new HashMap<>();
        a.prepareNewSession();
        a.log("", result, new HashMap<>());
        assertMapContains(result, "aa.sum", 68d);
        lr = new GenericLogRecord("test").addValue("size", 33d).addValue("ab", 34d);
        a.add(lr);
        a.prepareNewSession();
        a.log("", result, new HashMap<>());
        assertEquals(1000d, result.get("aa.sum"));
    }

    public void testCount() {
        Accumulator a = new AccumulatorExpressionParser().parse("count()");
        assertNotNull(a);
        assertEquals(SimpleCounter.class, a.getClass());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        Map<String, Double> result = new HashMap<>();
        a.prepareNewSession();
        a.log("", result, new HashMap<>());
        assertEquals(1d, result.get("count"));
    }

    public void testNamed() {
        Accumulator a = new AccumulatorExpressionParser().parse("name(toto,count())");
        assertNotNull(a);
        assertEquals(NamedAccumulator.class, a.getClass());

        NamedAccumulator na = (NamedAccumulator)a;
        assertEquals("toto", na.getRootName());
        assertNotNull(na.getAccumulator());
        assertEquals(SimpleCounter.class, na.getAccumulator().getClass());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        Map<String, Double> result = new HashMap<>();
        a.prepareNewSession();
        a.log("", result, new HashMap<>());
        assertMapContains(result, "toto.count", 4d);
    }

    public void testMulti() {
        Accumulator a = new AccumulatorExpressionParser().parse("multi([count(),sum(aa,0)])");
        assertNotNull(a);
        assertEquals(MultiAccumulator.class, a.getClass());

        MultiAccumulator na = (MultiAccumulator)a;
        assertEquals(2, na.getLength());
        assertNotNull(na.getAccumulator(0));
        assertNotNull(na.getAccumulator(1));
        assertEquals(SimpleCounter.class, na.getAccumulator(0).getClass());
        assertEquals(SumAccumulator.class, na.getAccumulator(1).getClass());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        Map<String, Double> result = new HashMap<>();
        a.prepareNewSession();
        a.log("", result, new HashMap<>());
        assertMapContains(result, "count", 4d);
        assertMapContains(result, "aa.sum", 34*4d);
    }

    public void testStringKey() {
        Accumulator a = new AccumulatorExpressionParser().parse("stringkey(uri,count())");
        assertNotNull(a);
        assertEquals(StringKeyAccumulator.class, a.getClass());

        StringKeyAccumulator na = (StringKeyAccumulator)a;
        assertEquals("uri", na.getDimensionName());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 33d).addValue("aa", 34d).addDimension("uri", "bleh");
        a.add(lr);
        a.add(lr);
        Map<String, Double> num = new HashMap<>();
        Map<String, String> str = new HashMap<>();
        a.prepareNewSession();
        a.log("", num, str);
        assertMapContains(num, "null.count", 4d);
        assertMapContains(num, "bleh.count", 2d);
        assertMapContains(str, "values", "null,bleh");
    }

    public void testStableKey() {
        Accumulator a = new AccumulatorExpressionParser().parse("stablekey(uri,count(),3)");
        assertNotNull(a);
        assertEquals(StableKeyAccumulator.class, a.getClass());

        StableKeyAccumulator na = (StableKeyAccumulator)a;
        assertEquals("uri", na.getDimensionName());
        assertEquals(3, (int)na.getMaxBuckets());

        LogRecord lr = new GenericLogRecord("test").addDimension("uri", "a");
        for (int i=0 ; i<10 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addDimension("uri", "bleh");
        for (int i=0 ; i<7 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addDimension("uri", "bluh");
        for (int i=0 ; i<2 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addDimension("uri", "blih");
        for (int i=0 ; i<1 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addDimension("uri", "blyh");
        for (int i=0 ; i<70 ; i++) a.add(lr);
        Map<String, Double> num = new HashMap<>();
        Map<String, Double> str = new HashMap<>();
        a.prepareNewSession();
        a.log("", num, str);
        assertMapContains(str, "values", "blyh,a,bleh,others");
        assertMapContains(num, "blyh.count", 70d);
        assertMapContains(num, "a.count", 10d);
        assertMapContains(num, "bleh.count", 7d);
        assertMapContains(num, "others.count", 3d);
    }

    public void testSimpleLog10Histogram() {
        Accumulator a = new AccumulatorExpressionParser().parse("loghist(size,10,3000)");
        assertNotNull(a);
        assertEquals(SimpleLogHistogram.class, a.getClass());

        SimpleLogHistogram na = (SimpleLogHistogram)a;
        assertEquals(4, na.getThresholds().size());
        assertEquals("size", na.getValueKey());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 0.5);
        for (int i=0 ; i<10 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 0.7);
        for (int i=0 ; i<9 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 55);
        for (int i=0 ; i<8 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 155);
        for (int i=0 ; i<7 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 1055);
        for (int i=0 ; i<6 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 100);
        for (int i=0 ; i<4 ; i++) a.add(lr);
        Map<String, Double> num = new HashMap<>();
        Map<String, Double> str = new HashMap<>();
        a.prepareNewSession();
        a.log("", num, str);
        assertMapContains(num, "1_d0.histValue", 19d);
        assertMapContains(num, "10_d0.histValue", 0d);
        assertMapContains(num, "100_d0.histValue", 12d);
        assertMapContains(num, "1000_d0.histValue", 7d);
        assertMapContains(num, "above.histValue", 6d);
        assertMapContains(str, "histValues", "1_d0,10_d0,100_d0,1000_d0,above");
    }

    public void testSimpleLog2Histogram() {
        Accumulator a = new AccumulatorExpressionParser().parse("loghist(size,2,3000)");
        assertNotNull(a);
        assertEquals(SimpleLogHistogram.class, a.getClass());

        SimpleLogHistogram na = (SimpleLogHistogram)a;
        assertEquals(12, na.getThresholds().size());
        assertEquals("size", na.getValueKey());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 0.5);
        for (int i=0 ; i<10 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 0.7);
        for (int i=0 ; i<9 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 55);
        for (int i=0 ; i<8 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 155);
        for (int i=0 ; i<7 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 1055);
        for (int i=0 ; i<6 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 100);
        for (int i=0 ; i<4 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 2500);
        for (int i=0 ; i<1 ; i++) a.add(lr);
        Map<String, Double> num = new HashMap<>();
        Map<String, Double> str = new HashMap<>();
        a.prepareNewSession();
        a.log("", num, str);
        assertMapContains(num, "1_d0.histValue", 19d);
        assertMapContains(num, "2_d0.histValue", 0d);
        assertMapContains(num, "4_d0.histValue", 0d);
        assertMapContains(num, "8_d0.histValue", 0d);
        assertMapContains(num, "16_d0.histValue", 0d);
        assertMapContains(num, "32_d0.histValue", 0d);
        assertMapContains(num, "64_d0.histValue", 8d);
        assertMapContains(num, "128_d0.histValue", 4d);
        assertMapContains(num, "256_d0.histValue", 7d);
        assertMapContains(num, "512_d0.histValue", 0d);
        assertMapContains(num, "1024_d0.histValue", 0d);
        assertMapContains(num, "2048_d0.histValue", 6d);
        assertMapContains(num, "above.histValue", 1d);
        assertMapContains(str, "histValues", "1_d0,2_d0,4_d0,8_d0,16_d0,32_d0,64_d0,128_d0,256_d0,512_d0,1024_d0,2048_d0,above");
    }

    public void testSimpleLog125Histogram() {
        Accumulator a = new AccumulatorExpressionParser().parse("log125hist(size,10,500)");
        assertNotNull(a);
        assertEquals(Simple125Histogram.class, a.getClass());

        Simple125Histogram na = (Simple125Histogram)a;
        assertEquals(6, na.getThresholds().size());
        assertEquals("size", na.getValueKey());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 0.5);
        for (int i=0 ; i<10 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 0.7);
        for (int i=0 ; i<9 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 55);
        for (int i=0 ; i<8 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 155);
        for (int i=0 ; i<7 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 1055);
        for (int i=0 ; i<6 ; i++) a.add(lr);
        lr = new GenericLogRecord("test").addValue("size", 100);
        for (int i=0 ; i<4 ; i++) a.add(lr);
        Map<String, Double> num = new HashMap<>();
        Map<String, Double> str = new HashMap<>();
        a.prepareNewSession();
        a.log("", num, str);
        assertMapContains(num, "10_d0.histValue", 19d);
        assertMapContains(num, "20_d0.histValue", 0d);
        assertMapContains(num, "50_d0.histValue", 0d);
        assertMapContains(num, "100_d0.histValue", 12d);
        assertMapContains(num, "200_d0.histValue", 7d);
        assertMapContains(num, "500_d0.histValue", 0d);
        assertMapContains(num, "above.histValue", 6d);
        assertMapContains(num, "count", 44d);
        assertMapContains(num, "total", 8266.3d);
        assertMapContains(num, "avg", 8266.3d/44);
        assertMapContains(str, "histValues", "10_d0,20_d0,50_d0,100_d0,200_d0,500_d0,above");
    }

    // TODO Needs some more cases
    public void testParsingErrors() {
        assertThrows(
                () -> new AccumulatorExpressionParser().parse("multi([sum(size,0),count(),sum(time,0)]"),
                ParseException.class,
                (ParseException e) -> {
                    assertEquals(39, e.getPosition());
                });
    }
}
