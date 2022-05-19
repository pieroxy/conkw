package net.pieroxy.conkw.accumulators;

import net.pieroxy.conkw.ConkwTestCase;

public class AccumulatorUtilsTest extends ConkwTestCase {
    public void testMetricPath() {
        helper("hello");
        helper("h.el.lo");
        helper("h_el_lo");
        helper("h,e,l,l,o");
        helper("h....ell,,,,o");
        helper("h...,__,,.,.,_,_.<_-.ell,,,,o");
    }

    private void helper(String s) {
        String s2 = AccumulatorUtils.cleanMetricPathElement(s);
        assertFalse(s2.contains("."));
        assertFalse(s2.contains(","));
        String s3 = AccumulatorUtils.parseMetricPathElement(s2);
        assertEquals(s, s3);
    }
}
