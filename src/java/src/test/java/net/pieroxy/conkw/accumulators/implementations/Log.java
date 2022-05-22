package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;

import java.util.HashMap;
import java.util.Map;

public class Log {
  Map<String, Double> values = new HashMap<>();
  Map<String, String> dimensions = new HashMap<>();

  public void assertEquals(Log log2, ConkwTestCase tc) {
    tc.assertEquals(log2.values.size(), values.size());
    tc.assertEquals(log2.dimensions.size(), dimensions.size());
    log2.values.entrySet().stream().forEach(e->tc.assertMapContains(log2.values, e.getKey(), e.getValue()));
    log2.dimensions.entrySet().stream().forEach(e->tc.assertMapContains(log2.dimensions, e.getKey(), e.getValue()));
  }
}
