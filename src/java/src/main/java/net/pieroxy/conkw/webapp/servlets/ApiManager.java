package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.webapp.grabbers.Grabber;
import net.pieroxy.conkw.webapp.grabbers.TriggerableGrabber;
import net.pieroxy.conkw.webapp.model.Response;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiManager implements MetaGrabber {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());

  private Map<String, Grabber> allGrabbers;

  Map<String, Long> lastRequestPerGrabber = new HashMap<>();

  public ApiManager(List<Grabber> grabbers) {
    allGrabbers = new HashMap<>();
    grabbers.forEach(g -> {allGrabbers.put(g.getName(), g);});
  }

  public void close() {
  }

  public Response buildResponse(long now, Collection<GrabberInput>grabbersRequested) {
    markGrabbersRequested(grabbersRequested, now);
    Response r = new Response();
    grabbersRequested.stream().parallel().forEach(
            s -> {
              Grabber g = allGrabbers.get(s.getName());
              if (g == null) {
                r.addError("Grabber '" + s.getName() + "' not found.");
              } else {
                r.add(g.grab(s.getParamValue()));
              }
            }
    );
    return r;
  }

  private void markGrabbersRequested(Collection<GrabberInput>grabbersRequested, long ts) {
    for (GrabberInput gin : grabbersRequested) {
      Long l = lastRequestPerGrabber.get(gin.getName());
      if (l==null) {
        Grabber g = allGrabbers.get(gin.getName());
        if (g==null) {
          LOGGER.log(Level.WARNING, "Grabber " + gin.getName() + " not found.");
        } else {
          Map<String, Long> newmap = new HashMap<>(lastRequestPerGrabber);
          newmap.put(gin.getName(), ts);
          lastRequestPerGrabber = newmap;
        }
      } else {
        lastRequestPerGrabber.put(gin.getName(), ts);
      }
    }
  }

  public String notifyGrabberAction(String action, Map<String, String[]> parameterMap) {
    final StringBuilder res = new StringBuilder();
    allGrabbers.values().forEach((g) -> {
      if (g.getName().equals(action)) {
        res.append(g.processAction(parameterMap));
      }
    });
    return res.toString();
  }
}

