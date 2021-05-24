package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.webapp.grabbers.Grabber;
import net.pieroxy.conkw.webapp.grabbers.TriggerableGrabber;
import net.pieroxy.conkw.webapp.model.Response;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiManager implements MetaGrabber {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());

  private Collection<Grabber> allGrabbers;

  Response loadedResponse;
  Thread thread;
  boolean stopped;
  Map<String, Long> lastRequestPerGrabber = new HashMap<>();

  private boolean smartUiGrabSync;

  public ApiManager(List<Grabber> grabbers) {
    allGrabbers = grabbers;
    thread = new Thread(this::run, "Api Thread");
    thread.start();
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
    Response r = new Response(loadedResponse, (int)(System.currentTimeMillis()%1000), grabbersRequested);
    return r;
  }

  private void markGrabbersRequested(String[]grabbersRequested, long ts) {
    for (String gname : grabbersRequested) {
      Long l = lastRequestPerGrabber.get(gname);
      if (l==null) {
        Map<String, Long> newmap = new HashMap<>(lastRequestPerGrabber);
        newmap.put(gname, ts);
        lastRequestPerGrabber = newmap;
      } else {
        lastRequestPerGrabber.put(gname, ts);
      }
    }
  }

  private void run() {
    while (thread == Thread.currentThread()) {
      long nowms= System.currentTimeMillis();

      Response r = new Response();
      for (Grabber g : allGrabbers) {
        try {
          if (isGrabberActive(g, nowms)) {
            r.add(g.grab());
            if (LOGGER.isLoggable(Level.FINE)) LOGGER.fine("Grabbing " + g.getName());
          }
        } catch (Exception e) {
          LOGGER.log(Level.SEVERE, "Grabber grab failed : " + g.toString(), e);
        }
      }
      loadedResponse = r;
      for (Grabber g : allGrabbers) {
        if (g instanceof TriggerableGrabber) {
          ((TriggerableGrabber) g).trigger(this);
        }
      }

      long wait;
      if (smartUiGrabSync) {
        long now = System.currentTimeMillis() % 1000;
        wait = 1000 - now;
        while (wait < 0) wait += 1000;
        while (wait > 1000) wait -= 1000;
        if (wait < 250) wait += 1000;
      } else {
        wait = 1000 + getRandomJitter();
      }

      synchronized (Api.class) {
        try {
          Api.class.wait(wait);
        } catch (InterruptedException e) {
        }
      }
    }
    LOGGER.info("Api Thread stopped.");
    stopped = true;
  }

  private static int getRandomJitter() {
    return (int)(Math.random()*50-25);
  }

  private boolean isGrabberActive(Grabber g, long now) {
    Long lastSeen = lastRequestPerGrabber.get(g.getName());
    return lastSeen!=null && (now-lastSeen < 3000);
  }

  public boolean isSmartUiGrabSync() {
    return smartUiGrabSync;
  }

  public void setSmartUiGrabSync(boolean smartUiGrabSync) {
    this.smartUiGrabSync = smartUiGrabSync;
  }

  public String notifyGrabberAction(String action, Map<String, String[]> parameterMap) {
    final StringBuilder res = new StringBuilder();
    allGrabbers.forEach((g) -> {
      if (g.getName().equals(action)) {
        res.append(g.processAction(parameterMap));
      }
    });
    return res.toString();
  }
}

