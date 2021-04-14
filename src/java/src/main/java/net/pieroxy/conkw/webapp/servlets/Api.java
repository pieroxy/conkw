package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.grabbers.Grabber;
import net.pieroxy.conkw.webapp.grabbers.TriggerableGrabber;
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

  private static ApiManager api;

  public static void setApiManager(ApiManager api) {
    Api.api = api;
  }


  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    long now = System.currentTimeMillis();
    final String action = req.getParameter("grabberAction");
    if (action!=null) {
      String res = api.notifyGrabberAction(action, req.getParameterMap());
      try {
        resp.getOutputStream().write(res.getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        LOGGER.log(Level.FINE, "Writing response", e);
      }
    } else {
      String grabbers = req.getParameter("grabbers");
      if (grabbers == null) {
        writeResponse(resp, Response.getError("Grabbers were not specified"));
      } else {
        Response r = api.buildResponse(now, grabbers);
        writeResponse(resp, r);
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
}

