package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.webapp.model.Response;

public interface MetaGrabber {
  Response buildResponse(long now, String grabbers);
}
