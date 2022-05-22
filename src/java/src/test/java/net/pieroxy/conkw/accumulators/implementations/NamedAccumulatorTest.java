package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.utils.PrefixedKeyMap;

public class NamedAccumulatorTest extends ConkwTestCase {
  public void testSimple() {
    NamedAccumulator<Data> na = new NamedAccumulator<>("toto", new SumAccumulator<>("value", 0));
    na.add(new Data().addVal("value", 12.));
    na.prepareNewSession();
    assertEquals(12., na.getAccumulator().getTotal());

    Data log = new Data();
    na.log("", log.getValues(), log.getDimensions());
    assertMapContains(log.getValues(), "toto.value.sum", 12.);
  }

  public void testInitializeFromData() {
    NamedAccumulator<Data> na = new NamedAccumulator("prefix", new SumAccumulator<>("vv.v", -10));
    na.add(new Data().addVal("vv.v", 12.));
    na.add(new Data().addVal("abc", 155.).addVal("vv.v", 7.));
    na.add(new Data().addVal("avvv", 7.));
    na.prepareNewSession();

    Data log = new Data();
    na.log("", log.getValues(), log.getDimensions());
    NamedAccumulator<Data> na2 = new NamedAccumulator("prefix", new SumAccumulator<>("vv.v", 0));
    na2.initializeFromData(new PrefixedKeyMap<>(log.getValues()), new PrefixedKeyMap<>(log.getDimensions()));
    na2.prepareNewSession();
    assertEquals(9., na2.getTotal());
    Data log2 = new Data();
    na2.log("", log2.getValues(), log2.getDimensions());
    log.assertEquals(log2, this);
  }

}
