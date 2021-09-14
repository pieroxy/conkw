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

  Thread thread;
  boolean stopped;
  Map<String, Long> lastRequestPerGrabber = new HashMap<>();

  public ApiManager(List<Grabber> grabbers) {
    allGrabbers = new HashMap<>();
    grabbers.forEach(g -> {allGrabbers.put(g.getName(), g);});
  }

  public void close() {
    thread = null;
    synchronized (Api.class) {
      Api.class.notifyAll();
    }
    while(!stopped) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
      }
    }
  }

  public Response buildResponse(long now, String grabbers) {
    String[] grabbersRequested = grabbers.split(",");
    markGrabbersRequested(grabbersRequested, now);
    Response r = new Response();
    Arrays.stream(grabbersRequested).parallel().forEach(
            s -> {
              Grabber g = allGrabbers.get(s);
              r.add(g.grab(null));
            }
    );
    return r;
  }

  private void markGrabbersRequested(String[]grabbersRequested, long ts) {
    for (String gname : grabbersRequested) {
      Long l = lastRequestPerGrabber.get(gname);
      if (l==null) {
        Grabber g = allGrabbers.get(gname);
        if (g==null) {
          LOGGER.log(Level.WARNING, "Grabber " + gname + " not found.");
        } else {
          Map<String, Long> newmap = new HashMap<>(lastRequestPerGrabber);
          newmap.put(gname, ts);
          lastRequestPerGrabber = newmap;
        }
      } else {
        lastRequestPerGrabber.put(gname, ts);
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

