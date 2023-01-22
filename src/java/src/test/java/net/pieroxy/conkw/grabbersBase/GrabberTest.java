package net.pieroxy.conkw.grabbersBase;

import net.pieroxy.conkw.ConkwTestCase;
import net.pieroxy.conkw.webapp.grabbers.ExternalInstanceGrabber;
import net.pieroxy.conkw.webapp.grabbers.ExternalMetricsGrabber;
import net.pieroxy.conkw.webapp.grabbers.FileGrabber;
import net.pieroxy.conkw.webapp.grabbers.JavaSystemViewGrabber;
import net.pieroxy.conkw.webapp.grabbers.http.HttpsCertGrabber;
import net.pieroxy.conkw.webapp.grabbers.oshi.OshiGrabber;
import net.pieroxy.conkw.webapp.grabbers.procgrabber.ProcGrabber;

public class GrabberTest extends ConkwTestCase {
  public void testGetConfigClass() {
    assertEquals(ProcGrabber.ProcGrabberConfig.class, new ProcGrabber().getConfigClass());
    assertEquals(OshiGrabber.OshiGrabberConfig.class, new OshiGrabber().getConfigClass());
    assertEquals(JavaSystemViewGrabber.JavaSystemViewGrabberConfig.class, new JavaSystemViewGrabber().getConfigClass());
    assertEquals(HttpsCertGrabber.HttpsCertGrabberConfig.class, new HttpsCertGrabber().getConfigClass());
    assertEquals(FileGrabber.FileGrabberConfig.class, new FileGrabber().getConfigClass());
    assertEquals(ExternalInstanceGrabber.ExternalInstanceGrabberConfig.class, new ExternalInstanceGrabber().getConfigClass());
    assertEquals(ExternalMetricsGrabber.ExternalMetricsGrabberConfig.class, new ExternalMetricsGrabber().getConfigClass());
  }
}
