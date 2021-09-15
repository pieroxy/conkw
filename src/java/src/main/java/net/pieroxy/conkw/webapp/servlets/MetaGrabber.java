package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.webapp.model.Response;

import java.util.Collection;

public interface MetaGrabber {
  Response buildResponse(long now, Collection<GrabberInput>grabbers);
}
