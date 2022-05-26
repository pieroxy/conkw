package net.pieroxy.conkw.accumulators.implementations;

import java.util.Arrays;
import java.util.Collection;

public class NamedAccumulatorTest extends AbstractAccumulatorTest<NamedAccumulator<Data>> {

  @Override
  protected NamedAccumulator<Data> buildAccumulator() {
    return new NamedAccumulator<>("toto", new SumAccumulator<>("vv.v", -10));
  }

  @Override
  protected Collection<Data> buildData() {
    return Arrays.asList(
            new Data().addVal("vv.v", 12.),
            new Data().addVal("abc", 155.).addVal("vv.v", 7.),
            new Data().addVal("avvv", 7.));
  }

  @Override
  protected void assertAccumulatorInternalState(NamedAccumulator<Data> acc) {
    assertEquals(9., acc.getTotal());
  }

  @Override
  protected void assertAccumulatorLog(Data log) {
    assertMapContains(log.getValues(), "toto.vv_dv.sum", 9.);
  }
}
