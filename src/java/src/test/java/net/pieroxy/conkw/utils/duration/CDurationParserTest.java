package net.pieroxy.conkw.utils.duration;

import net.pieroxy.conkw.ConkwTestCase;

public class CDurationParserTest extends ConkwTestCase {
    public void testSimple() {
        assertEquals(7, CDurationParser.parse("7s").asSeconds());
        assertEquals(300, CDurationParser.parse("5m").asSeconds());
        assertEquals(3000, CDurationParser.parse("50m").asSeconds());
        assertEquals(7200, CDurationParser.parse("2h").asSeconds());
        assertEquals(7*24, CDurationParser.parse("7d").asHours());
        assertEquals(365*8, CDurationParser.parse("8y").asDays());
    }
    public void testAs() {
        assertEquals(365*8*24*60*60, CDurationParser.parse("8y").asSeconds());
        assertEquals(365*8*24*60, CDurationParser.parse("8y").asMinutes());
        assertEquals(365*8*24, CDurationParser.parse("8y").asHours());
        assertEquals(365*8, CDurationParser.parse("8y").asDays());
        assertEquals(8, CDurationParser.parse("8y").asYears());
    }
}
