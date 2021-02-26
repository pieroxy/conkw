package net.pieroxy.conkw.webapp.grabbers.procgrabber;

import junit.framework.TestCase;

import java.io.File;

public class MdstatParserTest extends TestCase {
  public void testMdstat() {
    MdstatParser.MdstatResult res = MdstatParser.parseMdstat(new File("../../test-data/mdstat/a"));
    assertEquals(1, res.getIndividual().size());
    assertEquals("md_d0:[5/5]", res.getIndividual().get(0));
    assertEquals("md_d0:[5/5]", res.getOneline());
    assertEquals(0, res.getFailedDisks());
    res = MdstatParser.parseMdstat(new File("../../test-data/mdstat/b"));
    assertEquals(1, res.getIndividual().size());
    assertEquals("md0:[4/3]", res.getIndividual().get(0));
    assertEquals("md0:[4/3]", res.getOneline());
    assertEquals(0, res.getFailedDisks());
    res = MdstatParser.parseMdstat(new File("../../test-data/mdstat/c"));
    assertEquals(4, res.getIndividual().size());
    assertEquals("md1:[2/2]", res.getIndividual().get(0));
    assertEquals("md2:[2/2]", res.getIndividual().get(1));
    assertEquals("md3:[10/10]", res.getIndividual().get(2));
    assertEquals("md0:[2/2]", res.getIndividual().get(3));
    assertEquals("md1:[2/2] md2:[2/2] md3:[10/10] md0:[2/2]", res.getOneline());
    assertEquals(0, res.getFailedDisks());
    res = MdstatParser.parseMdstat(new File("../../test-data/mdstat/d"));
    assertEquals(1, res.getIndividual().size());
    assertEquals("md127:[6/5]<13%>", res.getIndividual().get(0));
    assertEquals("md127:[6/5]<13%>", res.getOneline());
    assertEquals(0, res.getFailedDisks());
    res = MdstatParser.parseMdstat(new File("../../test-data/mdstat/e"));
    assertEquals(1, res.getIndividual().size());
    assertEquals("md0:[7/7]", res.getIndividual().get(0));
    assertEquals("md0:[7/7]", res.getOneline());
    assertEquals(0, res.getFailedDisks());
    res = MdstatParser.parseMdstat(new File("../../test-data/mdstat/f"));
    assertEquals(1, res.getIndividual().size());
    assertEquals("md1:[6/4]", res.getIndividual().get(0));
    assertEquals("md1:[6/4]", res.getOneline());
    assertEquals(1, res.getFailedDisks());
    res = MdstatParser.parseMdstat(new File("../../test-data/mdstat/g"));
    assertEquals(0, res.getIndividual().size());
    assertEquals("", res.getOneline());
    res = MdstatParser.parseMdstat(new File("../../test-data/mdstat/h"));
    assertEquals(1, res.getIndividual().size());
    assertEquals("md0:[6/5]", res.getIndividual().get(0));
    assertEquals("md0:[6/5]", res.getOneline());
    assertEquals(1, res.getFailedDisks());
  }
}
