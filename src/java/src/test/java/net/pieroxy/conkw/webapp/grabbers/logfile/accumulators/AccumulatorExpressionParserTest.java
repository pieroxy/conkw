package net.pieroxy.conkw.webapp.grabbers.logfile.accumulators;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.webapp.grabbers.logfile.LogRecord;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations.SimpleCounter;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations.StringKeyAccumulator;
import net.pieroxy.conkw.webapp.grabbers.logfile.accumulators.implementations.SumAccumulator;
import net.pieroxy.conkw.webapp.grabbers.logfile.parsers.GenericLogRecord;

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
        a.log("", result, new HashMap<>());
        assertMapContains(result, "aa.sum", 68d);
        lr = new GenericLogRecord("test").addValue("size", 33d).addValue("ab", 34d);
        a.add(lr);
        a.log("", result, new HashMap<>());
        assertEquals(1068d, result.get("aa.sum"));
    }

    public void testCount() {
        Accumulator a = new AccumulatorExpressionParser().parse("count()");
        assertNotNull(a);
        assertEquals(SimpleCounter.class, a.getClass());

        LogRecord lr = new GenericLogRecord("test").addValue("size", 33d).addValue("aa", 34d);
        a.add(lr);
        Map<String, Double> result = new HashMap<>();
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
        a.log("", result, new HashMap<>());
        assertMapContains(result, "count", 4d);
        assertMapContains(result, "aa.sum", 34*4d);
    }

    public void testStringKey() {
        Accumulator a = new AccumulatorExpressionParser().parse("skey(uri,count())");
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
        Map<String, Double> result = new HashMap<>();
        a.log("", result, new HashMap<>());
        assertMapContains(result, "null.count", 4d);
        assertMapContains(result, "bleh.count", 2d);
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
