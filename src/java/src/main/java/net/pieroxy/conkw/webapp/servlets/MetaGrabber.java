package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.webapp.model.MetricsApiResponse;

import java.util.Collection;

public interface MetaGrabber {
  MetricsApiResponse buildResponse(long now, Collection<GrabberInput>grabbers);
}
