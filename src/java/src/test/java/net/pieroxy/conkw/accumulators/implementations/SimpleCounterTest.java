package net.pieroxy.conkw.accumulators.implementations;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.utils.PrefixedKeyMap;

public class SimpleCounterTest extends ConkwTestCase {
  public void testSimple() {
    SimpleCounter<Data> sa = new SimpleCounter<>();
    sa.add(new Data().addVal("vvv", 12.));
    sa.add(new Data().addVal("abc", 155.).addVal("vvv", 7.));
    sa.add(new Data().addVal("avvv", 7.));
    sa.prepareNewSession();
    assertEquals(3., sa.getTotal());
  }
  public void testInitializeFromData() {
    SimpleCounter<Data> sa = new SimpleCounter<>();
    for (int i=0 ; i<123 ; i++) sa.add(null);
    sa.prepareNewSession();

    Data log = new Data();
    sa.log("", log.getValues(), log.getDimensions());
    SimpleCounter sa2 = new SimpleCounter();
    sa2.initializeFromData(new PrefixedKeyMap<>(log.getValues()), new PrefixedKeyMap<>(log.getDimensions()));
    sa2.prepareNewSession();
    assertEquals(123., sa2.getTotal());
    Data log2 = new Data();
    sa2.log("", log2.getValues(), log2.getDimensions());
    log.assertEquals(log2, this);
  }
}
