package net.pieroxy.conkw.utils;

import junit.framework.TestCase;
import net.pieroxy.conkw.ConkwTestCase;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PrefixedKeyMapTest extends ConkwTestCase {

    public void testEmpty() {
        PrefixedKeyMap<String> pkm = new PrefixedKeyMap<>(new HashMap<>());
        assertEquals(0, pkm.size());
        assertEquals(true, pkm.isEmpty());
        pkm.setPrefix("toto");
        assertEquals(0, pkm.size());
        assertEquals(true, pkm.isEmpty());
    }

    public void testOneEntry() {
        Map<String, String> map = new TreeMap<>();
        map.put("toti", "tutu");
        PrefixedKeyMap<String> pkm = new PrefixedKeyMap<>(map);
        assertEquals("tutu", pkm.get("toti"));
        assertEquals(1, pkm.size());
        assertEquals(false, pkm.isEmpty());
        assertEquals("toti", pkm.keySet().stream().collect(Collectors.joining(",")));
        pkm.setPrefix("to");
        assertEquals(1, pkm.size());
        assertEquals(false, pkm.isEmpty());
        assertEquals("tutu", pkm.get("ti"));
        assertEquals("ti", pkm.keySet().stream().collect(Collectors.joining(",")));
        pkm.setPrefix("tu");
        assertEquals(0, pkm.size());
        assertEquals(true, pkm.isEmpty());
        assertEquals("", pkm.keySet().stream().collect(Collectors.joining(",")));
    }

    public void testMultipleEntries() {
        Map<String, String> map = new HashMap<>();
        map.put("toti", "tutu");
        map.put("tota", "totatutu");
        map.put("toast", "bread");
        map.put("tortoise", "animal");
        map.put("2.1", "twentyone");
        map.put("2.2", "twentytwo");
        map.put("2.3", "twentythree");

        PrefixedKeyMap<String> pkm = new PrefixedKeyMap<>(map);
        assertMapContains(pkm, "toti", "tutu");
        assertMapContains(pkm, "tota", "totatutu");
        assertMapContains(pkm, "toast", "bread");
        assertMapContains(pkm, "tortoise", "animal");
        assertMapContains(pkm, "2.1", "twentyone");
        assertMapContains(pkm, "2.2", "twentytwo");
        assertMapContains(pkm, "2.3", "twentythree");
        assertEquals(7, pkm.size());
        assertEquals(false, pkm.isEmpty());
        assertEquals("2.1,2.2,2.3,toast,tortoise,tota,toti", pkm.keySet().stream().sorted().collect(Collectors.joining(",")));

        pkm.setPrefix("tot");
        assertEquals(2, pkm.size());
        assertEquals(false, pkm.isEmpty());
        assertMapContains(pkm, "i", "tutu");
        assertMapContains(pkm, "a", "totatutu");
        assertEquals("a,i", pkm.keySet().stream().sorted().collect(Collectors.joining(",")));

        pkm.setPrefix("2.");
        assertEquals(3, pkm.size());
        assertEquals(false, pkm.isEmpty());
        assertMapContains(pkm, "1", "twentyone");
        assertMapContains(pkm, "2", "twentytwo");
        assertMapContains(pkm, "3", "twentythree");
        assertEquals("1,2,3", pkm.keySet().stream().collect(Collectors.joining(",")));
        assertEquals("twentyone,twentythree,twentytwo", pkm.values().stream().sorted().collect(Collectors.joining(",")));
    }
}
