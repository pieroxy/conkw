package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.Listener;
import net.pieroxy.conkw.webapp.grabbers.Grabber;
import net.pieroxy.conkw.webapp.model.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Api extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());

  private static Collection<Grabber> grabbers;

  static Response loadedResponse;
  static Thread thread;
  static boolean stopped;
  static long lastGet =  System.currentTimeMillis();

  public static synchronized void setGrabbers(Collection<Grabber>g) {
    if (thread == null) {
      thread = new Thread(Api::run, "Api Thread");
      thread.start();
    }
    grabbers = g;
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
    lastGet =  System.currentTimeMillis();
    String action = req.getParameter("_grabber_");
    if (action!=null) {
      grabbers.forEach((g) -> {if (g.getName().equals(action)) {
        String res = g.processAction(req.getParameterMap());
        try {
          resp.getOutputStream().write(res.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
          LOGGER.log(Level.SEVERE, "Grabbing " + g.getName(), e);
        }
      }
      });
    } else {
      for (Grabber g : grabbers) g.processHttp(req);
      writeResponse(resp);
    }
  }

  private void writeResponse(HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json;charset=utf-8");
    DslJson<Object> json = JsonHelper.getJson();
    JsonWriter w = JsonHelper.getWriter();
    synchronized (w) {
      //synchronized (loadedResponse) { -> Probably no need. int assignment is atomic and if two threads are concurrently there they will have ~ the same timestamp.
        loadedResponse.setResponseJitter((int)(System.currentTimeMillis()%1000));
        w.reset(resp.getOutputStream());
        json.serialize(w, loadedResponse);
        w.flush();
      //}
    }
  }

  private static void run() {
    while (thread == Thread.currentThread()) {
      long nowms= System.currentTimeMillis();

      if (nowms-lastGet < 2100) {
        Response r = new Response();
        for (Grabber g : grabbers) {
          try {
            r.add(g.grab());
          } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Grabber grab failed : " + g.toString(), e);
          }
        }
        loadedResponse = r;
      }

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
}

