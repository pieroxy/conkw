package net.pieroxy.conkw.accumulators.implementations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleCounterTest extends AbstractAccumulatorTest<SimpleCounter<Data>> {

  public void testSimple() {
    SimpleCounter<Data> sa = new SimpleCounter<>();
    sa.add(new Data().addVal("vvv", 12.));
    sa.add(new Data().addVal("abc", 155.).addVal("vvv", 7.));
    sa.add(new Data().addVal("avvv", 7.));
    sa.prepareNewSession();
    assertEquals(3., sa.getTotal());
  }

  @Override
  protected SimpleCounter<Data> buildAccumulator() {
    return new SimpleCounter<>();
  }

  @Override
  protected Collection<Data> buildData() {
    List<Data> ld = new ArrayList<>();
    for (int i=0 ; i<123 ; i++) ld.add(null);
    return ld;
  }

  @Override
  protected void assertAccumulatorInternalState(SimpleCounter<Data> acc) {
    assertEquals(123., acc.getTotal());
  }

  @Override
  protected void assertAccumulatorLog(Data log) {
    assertMapContains(log.getValues(), "count", 123.);
  }
}
