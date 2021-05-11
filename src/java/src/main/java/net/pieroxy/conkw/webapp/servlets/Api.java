package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.config.ApiAuth;
import net.pieroxy.conkw.config.Config;
import net.pieroxy.conkw.config.User;
import net.pieroxy.conkw.utils.HashTools;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.model.NeedsAuthResponse;
import net.pieroxy.conkw.webapp.model.Response;
import net.pieroxy.conkw.webapp.model.ResponseData;
import net.pieroxy.conkw.webapp.servlets.auth.ChallengeResponse;
import net.pieroxy.conkw.webapp.servlets.auth.Session;
import net.pieroxy.conkw.webapp.servlets.auth.Sessions;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Api extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());

  private static ApiManager api;
  private static ApiAuthManager auth;

  public static void setContext(ApiManager api, ApiAuth authConfig, File dataDir) {
    Api.api = api;
    if (auth==null) {
      auth = new ApiAuthManager();
    }
    auth.applyConfig(authConfig, dataDir);
  }

  public static void close() {
    auth.close();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (!auth.isAuthOk(req)) {
      writeResponse(resp, auth.performAuthentication(req));
      return;
    }

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

  private void writeResponse(HttpServletResponse resp, Object r) throws IOException {
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

