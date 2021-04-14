package net.pieroxy.conkw.webapp.grabbers;

import net.pieroxy.conkw.webapp.servlets.MetaGrabber;

public interface TriggerableGrabber {
  void trigger(MetaGrabber grabber);
}
