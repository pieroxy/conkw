package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.accumulators.implementations.*;
import net.pieroxy.conkw.accumulators.parser.AccumulatorExpressionParser;
import net.pieroxy.conkw.accumulators.parser.ParseException;
import net.pieroxy.conkw.pub.mdlog.DataRecord;
import net.pieroxy.conkw.pub.mdlog.GenericLogRecord;

public class AccumulatorExpressionParserTest extends ConkwTestCase {
    public void testSum() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("sum(aa,1000)"));
        assertNotNull(a);
        assertEquals(SumAccumulator.class, a.getClass());
        SumAccumulator sa = (SumAccumulator) a;
        assertEquals("aa", sa.getValueName());

        DataRecord lr = new GenericLogRecord().addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        a.add(lr);
        DataRecord result = new Data();
        a.prepareNewSession();
        a.log("", result);
        assertMapContains(result.getValues(), "aa.sum", 68d);
        lr = new GenericLogRecord().addValue("size", 33d).addValue("ab", 34d);
        a.add(lr);
        a.prepareNewSession();
        a.log("", result);
        assertEquals(1000d, result.getValues().get("aa.sum"));
    }

    public void testCount() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("count()"));
        assertNotNull(a);
        assertEquals(SimpleCounter.class, a.getClass());

        DataRecord lr = new GenericLogRecord().addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        DataRecord result = new Data();
        a.prepareNewSession();
        a.log("", result);
        assertEquals(1d, result.getValues().get("count"));
    }

    public void testNamed() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("name(toto,count())"));
        assertNotNull(a);
        assertEquals(NamedAccumulator.class, a.getClass());

        NamedAccumulator na = (NamedAccumulator)a;
        assertEquals("toto", na.getRootName());
        assertNotNull(na.getAccumulator());
        assertEquals(SimpleCounter.class, na.getAccumulator().getClass());

        DataRecord lr = new GenericLogRecord().addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        DataRecord result = new Data();
        a.prepareNewSession();
        a.log("", result);
        assertMapContains(result.getValues(), "toto.count", 4d);
    }

    public void testMulti() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("multi([count(),sum(aa,0)])"));
        assertNotNull(a);
        assertEquals(MultiAccumulator.class, a.getClass());

        MultiAccumulator na = (MultiAccumulator)a;
        assertEquals(2, na.getLength());
        assertNotNull(na.getAccumulator(0));
        assertNotNull(na.getAccumulator(1));
        assertEquals(SimpleCounter.class, na.getAccumulator(0).getClass());
        assertEquals(SumAccumulator.class, na.getAccumulator(1).getClass());

        DataRecord lr = new GenericLogRecord().addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        DataRecord result = new Data();
        a.prepareNewSession();
        a.log("", result);
        assertMapContains(result.getValues(), "count", 4d);
        assertMapContains(result.getValues(), "aa.sum", 34*4d);
    }

    public void testStringKey() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("stringkey(uri,count())"));
        assertNotNull(a);
        assertEquals(StringKeyAccumulator.class, a.getClass());

        StringKeyAccumulator na = (StringKeyAccumulator)a;
        assertEquals("uri", na.getDimensionName());

        DataRecord lr = new GenericLogRecord().addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        a.add(lr);
        lr = new GenericLogRecord().addValue("size", 33d).addValue("aa", 34d).addDimension("uri", "bleh");
        a.add(lr);
        a.add(lr);
        Data data = new Data();
        a.prepareNewSession();
        a.log("", data);
        assertMapContains(data.getValues(), "null.count", 4d);
        assertMapContains(data.getValues(), "bleh.count", 2d);
        assertMapContains(data.getDimensions(), "values", "null,bleh");
    }

    public void testStableKey() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("stablekey(uri,count(),3)"));
        assertNotNull(a);
        assertEquals(StableKeyAccumulator.class, a.getClass());

        StableKeyAccumulator na = (StableKeyAccumulator)a;
        assertEquals("uri", na.getDimensionName());
        assertEquals(3, (int)na.getMaxBuckets());

        DataRecord lr = new GenericLogRecord().addDimension("uri", "a");
        for (int i=0 ; i<10 ; i++) a.add(lr);
        lr = new GenericLogRecord().addDimension("uri", "bleh");
        for (int i=0 ; i<7 ; i++) a.add(lr);
        lr = new GenericLogRecord().addDimension("uri", "bluh");
        for (int i=0 ; i<2 ; i++) a.add(lr);
        lr = new GenericLogRecord().addDimension("uri", "blih");
        for (int i=0 ; i<1 ; i++) a.add(lr);
        lr = new GenericLogRecord().addDimension("uri", "blyh");
        for (int i=0 ; i<70 ; i++) a.add(lr);
        Data data = new Data();
        a.prepareNewSession();
        a.log("", data);
        assertMapContains(data.getDimensions(), "values", "blyh,a,bleh,others");
        assertMapContains(data.getValues(), "blyh.count", 70d);
        assertMapContains(data.getValues(), "a.count", 10d);
        assertMapContains(data.getValues(), "bleh.count", 7d);
        assertMapContains(data.getValues(), "others.count", 3d);
    }

    public void testSimpleLog10Histogram() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("loghist(size,10,3000)"));
        assertNotNull(a);
        assertEquals(SimpleLogHistogram.class, a.getClass());

        SimpleLogHistogram na = (SimpleLogHistogram)a;
        assertEquals(4, na.getThresholds().size());
        assertEquals("size", na.getValueKey());

        DataRecord lr = new GenericLogRecord().addValue("size", 0.5);
        for (int i=0 ; i<10 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 0.7);
        for (int i=0 ; i<9 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 55);
        for (int i=0 ; i<8 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 155);
        for (int i=0 ; i<7 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 1055);
        for (int i=0 ; i<6 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 100);
        for (int i=0 ; i<4 ; i++) a.add(lr);
        Data data = new Data();
        a.prepareNewSession();
        a.log("", data);
        assertMapContains(data.getValues(), "1.histValue", 19d);
        assertMapContains(data.getValues(), "10.histValue", 0d);
        assertMapContains(data.getValues(), "100.histValue", 12d);
        assertMapContains(data.getValues(), "1000.histValue", 7d);
        assertMapContains(data.getValues(), "above.histValue", 6d);
        assertMapContains(data.getDimensions(), "histValues", "1,10,100,1000,above");
    }

    public void testSimpleLog2Histogram() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("loghist(size,2,3000)"));
        assertNotNull(a);
        assertEquals(SimpleLogHistogram.class, a.getClass());

        SimpleLogHistogram na = (SimpleLogHistogram)a;
        assertEquals(12, na.getThresholds().size());
        assertEquals("size", na.getValueKey());

        DataRecord lr = new GenericLogRecord().addValue("size", 0.5);
        for (int i=0 ; i<10 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 0.7);
        for (int i=0 ; i<9 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 55);
        for (int i=0 ; i<8 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 155);
        for (int i=0 ; i<7 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 1055);
        for (int i=0 ; i<6 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 100);
        for (int i=0 ; i<4 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 2500);
        for (int i=0 ; i<1 ; i++) a.add(lr);
        Data data = new Data();
        a.prepareNewSession();
        a.log("", data);
        assertMapContains(data.getValues(), "1.histValue", 19d);
        assertMapContains(data.getValues(), "2.histValue", 0d);
        assertMapContains(data.getValues(), "4.histValue", 0d);
        assertMapContains(data.getValues(), "8.histValue", 0d);
        assertMapContains(data.getValues(), "16.histValue", 0d);
        assertMapContains(data.getValues(), "32.histValue", 0d);
        assertMapContains(data.getValues(), "64.histValue", 8d);
        assertMapContains(data.getValues(), "128.histValue", 4d);
        assertMapContains(data.getValues(), "256.histValue", 7d);
        assertMapContains(data.getValues(), "512.histValue", 0d);
        assertMapContains(data.getValues(), "1024.histValue", 0d);
        assertMapContains(data.getValues(), "2048.histValue", 6d);
        assertMapContains(data.getValues(), "above.histValue", 1d);
        assertMapContains(data.getDimensions(), "histValues", "1,2,4,8,16,32,64,128,256,512,1024,2048,above");
    }

    public void testSimpleLog125Histogram() {
        Accumulator a = getParsedAccumulator(new AccumulatorExpressionParser().parse("log125hist(size,10,500)"));
        assertNotNull(a);
        assertEquals(Simple125Histogram.class, a.getClass());

        Simple125Histogram na = (Simple125Histogram)a;
        assertEquals(6, na.getThresholds().size());
        assertEquals("size", na.getValueKey());

        DataRecord lr = new GenericLogRecord().addValue("size", 0.5);
        for (int i=0 ; i<10 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 0.7);
        for (int i=0 ; i<9 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 55);
        for (int i=0 ; i<8 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 155);
        for (int i=0 ; i<7 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 1055);
        for (int i=0 ; i<6 ; i++) a.add(lr);
        lr = new GenericLogRecord().addValue("size", 100);
        for (int i=0 ; i<4 ; i++) a.add(lr);
        Data data = new Data();
        a.prepareNewSession();
        a.log("", data);
        assertMapContains(data.getValues(), "10.histValue", 19d);
        assertMapContains(data.getValues(), "20.histValue", 0d);
        assertMapContains(data.getValues(), "50.histValue", 0d);
        assertMapContains(data.getValues(), "100.histValue", 12d);
        assertMapContains(data.getValues(), "200.histValue", 7d);
        assertMapContains(data.getValues(), "500.histValue", 0d);
        assertMapContains(data.getValues(), "above.histValue", 6d);
        assertMapContains(data.getValues(), "count", 44d);
        assertMapContains(data.getValues(), "total", 8266.3d);
        assertMapContains(data.getValues(), "avg", 8266.3d/44);
        assertMapContains(data.getDimensions(), "histValues", "10,20,50,100,200,500,above");
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

    private <T extends DataRecord> Accumulator<T> getParsedAccumulator(Accumulator<T> acc) {
        if (acc instanceof RootAccumulator) {
            return ((RootAccumulator<T>) acc).getAccumulator();
        }
        return acc;
    }
}
