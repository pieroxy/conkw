package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;

public class NamedAccumulatorTest extends ConkwTestCase {
  public void testSimple() {
    NamedAccumulator<Data> na = new NamedAccumulator<>("toto", new SumAccumulator<>("value", 0));
    na.add(new Data().addVal("value", 12.));
    na.prepareNewSession();
    assertEquals(12., na.getAccumulator().getTotal());

    Log log = new Log();
    na.log("", log.values, log.dimensions);
    assertMapContains(log.values, "toto.value.sum", 12.);
  }
}
