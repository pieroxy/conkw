package net.pieroxy.conkw.utils.duration;

import net.pieroxy.conkw.ConkwTestCase;

public class CDurationParserTest extends ConkwTestCase {
    public void testSimple() {
        assertEquals(7, CDurationParser.parse("7000ms").asSeconds());
        assertEquals(7, CDurationParser.parse("7s").asSeconds());
        assertEquals(300, CDurationParser.parse("5m").asSeconds());
        assertEquals(3000, CDurationParser.parse("50m").asSeconds());
        assertEquals(7200, CDurationParser.parse("2h").asSeconds());
        assertEquals(7*24, CDurationParser.parse("7d").asHours());
        assertEquals(365*8, CDurationParser.parse("8y").asDays());
    }
    public void testAs() {
        assertEquals(8, CDurationParser.parse("8y").asYears());
        assertEquals(365*8, CDurationParser.parse("8y").asDays());
        assertEquals(365*8*24, CDurationParser.parse("8y").asHours());
        assertEquals(365*8*24*60, CDurationParser.parse("8y").asMinutes());
        assertEquals(365*8*24*60*60, CDurationParser.parse("8y").asSeconds());
        assertEquals(365*8*24*60*60*1000l, CDurationParser.parse("8y").asMilliseconds());
    }
    public void testErrors() {
        assertFalse(CDurationParser.parse("a1s").isValid());
        assertFalse(CDurationParser.parse("abcd").isValid());
        assertFalse(CDurationParser.parse("").isValid());
        assertFalse(CDurationParser.parse(null).isValid());
        assertFalse(CDurationParser.parse("34").isValid());
    }
    public void testValid() {
        assertTrue(CDurationParser.parse("7000ms").isValid());
        assertTrue(CDurationParser.parse("7s").isValid());
        assertTrue(CDurationParser.parse("5m").isValid());
        assertTrue(CDurationParser.parse("50m").isValid());
        assertTrue(CDurationParser.parse("2h").isValid());
        assertTrue(CDurationParser.parse("7d").isValid());
        assertTrue(CDurationParser.parse("8y").isValid());
    }
}
