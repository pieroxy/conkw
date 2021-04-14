package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.grabbers.Grabber;
import net.pieroxy.conkw.webapp.model.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Api extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());

  private static Collection<Grabber> allGrabbers;

  static Response loadedResponse;
  static Thread thread;
  static boolean stopped;
  static Map<String, Long> lastRequestPerGrabber = new HashMap<>();

  public static synchronized void setAllGrabbers(Collection<Grabber>g) {
    if (thread == null) {
      thread = new Thread(Api::run, "Api Thread");
      thread.start();
    }
    allGrabbers = g;
  }

  public static void close() {
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

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    long now = System.currentTimeMillis();
    final String action = req.getParameter("grabberAction");
    if (action!=null) {
      allGrabbers.forEach((g) -> {
        if (g.getName().equals(action)) {
          String res = g.processAction(req.getParameterMap());
          try {
            resp.getOutputStream().write(res.getBytes(StandardCharsets.UTF_8));
          } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Grabbing " + g.getName(), e);
          }
        }
      });
    } else {
      String grabbers = req.getParameter("grabbers");
      if (grabbers == null) {
        writeResponse(resp, Response.getError("Grabbers were not specified"));
      } else {
        Response r = buildResponse(now, grabbers);
        writeResponse(resp, r);
      }
    }
  }

  private Response buildResponse(long now, String grabbers) {
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

  private void writeResponse(HttpServletResponse resp, Response r) throws IOException {
    resp.setContentType("application/json;charset=utf-8");
    DslJson<Object> json = JsonHelper.getJson();
    JsonWriter w = JsonHelper.getWriter();
    synchronized (w) {
      w.reset(resp.getOutputStream());
      json.serialize(w, r);
      w.flush();
    }
  }

  private static void run() {
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

      long now= System.currentTimeMillis() % 1000;
      long wait = 1000-now;
      while (wait<0) wait+=1000;
      while (wait>1000) wait-=1000;
      if (wait<250) wait+=1000;

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

  private static boolean isGrabberActive(Grabber g, long now) {
    Long lastSeen = lastRequestPerGrabber.get(g.getName());
    return lastSeen!=null && (now-lastSeen < 3000);
  }
}

