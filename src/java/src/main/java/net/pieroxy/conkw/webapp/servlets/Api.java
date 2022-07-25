package net.pieroxy.conkw.webapp.servlets;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import net.pieroxy.conkw.standalone.InstanceId;
import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.webapp.Filter;
import net.pieroxy.conkw.webapp.model.MetricsApiResponse;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Api extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(Api.class.getName());
  public static final String SHUTDOWN_PARAMETER = "shutdown";

  private static InstanceId instanceId;
  private static Runnable shutdown;

  private final Services services;

  public Api(Services services) {
    this.services = services;
  }

  public static void configureShutdownHook(InstanceId iid, Runnable shutdown) {
    instanceId = iid;
    Api.shutdown = shutdown;
  }

  private boolean shouldShutdown(HttpServletRequest req) {
    int rs = req.getParameterMap().size();
    if (rs!=1) return false;
    boolean sp = req.getParameter(SHUTDOWN_PARAMETER)==null;
    if (sp) return false;
    boolean ss = instanceId.getKey().equals(req.getParameter(SHUTDOWN_PARAMETER));
    if (ss) {
      LOGGER.warning("Shutdown sequence requested.");
      return true;
    } else {
      LOGGER.warning("Shutdown sequence requested with the wrong key.");
      return false;
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if (shouldShutdown(req)) {
      req.setAttribute(Filter.API_VERB, "shutdown");
      writeResponse(resp, "OK");
      new Timer().schedule(new TimerTask() {
        @Override
        public void run() {
          shutdown.run();
        }
      }, 500L);;
      return;
    }
    String sid = req.getParameter(ApiAuthManager.SID_FIELD);
    if (!services.getApiAuthManager().isAuthOk(sid)) {
      String user = req.getParameter(ApiAuthManager.USER_FIELD);
      String pass = req.getParameter(ApiAuthManager.PASS_FIELD);

      writeResponse(resp, services.getApiAuthManager().performAuthentication(user, pass));
      return;
    }

    long now = System.currentTimeMillis();
    final String action = req.getParameter("grabberAction");
    if (action!=null) {
      req.setAttribute(Filter.API_VERB, "grabberAction");
      String res = services.getApiManager().notifyGrabberAction(action, req.getParameterMap());
      try {
        resp.getOutputStream().write(res.getBytes(StandardCharsets.UTF_8));
      } catch (IOException e) {
        LOGGER.log(Level.FINE, "Writing response", e);
      }
    } else {
      req.setAttribute(Filter.API_VERB, "grabbers");
      String grabbers = req.getParameter("grabbers");
      if (grabbers == null) {
        writeResponse(resp, MetricsApiResponse.getError("Grabbers were not specified"));
      } else {
        GrabberInput.InputHolder in = getInput(grabbers, req);
        try (MetricsApiResponse r = services.getApiManager().buildResponse(now, in.in)) {
          if (in.errors != null) in.errors.stream().forEach(r::addError);
          writeResponse(resp, r);
        }
      }
    }
  }

  private void addError(GrabberInput.InputHolder h, String error) {
    if (h.errors == null) h.errors = new ArrayList<>();
    h.errors.add(error);

  }

  GrabberInput.InputHolder getInput(String grabbers, HttpServletRequest req) {
    GrabberInput.InputHolder res = new GrabberInput.InputHolder();
    String[]gnames = grabbers.split(",");
    res.in = new ArrayList<>(gnames.length);
    Arrays.stream(gnames).forEach(s -> {
      try {
        GrabberInput gin = new GrabberInput(s);
        if (gin.getParamName() != null) {
          String pv = req.getParameter(gin.getParamName());
          if (pv == null) {
            addError(res,"Parameter '" + gin.getParamName() + "' for grabber '" + gin.getName() + "' not found.");
          } else {
            gin.setParamValue(pv);
          }
        }
        res.in.add(gin);
      } catch (Exception e) {
        addError(res, "Could not parse grabber name '"+s+"': " + e.getMessage());
        LOGGER.log(Level.SEVERE, "Could not parse grabber name: " + s, e);
      }
    });
    return res;
  }

  private void writeResponse(HttpServletResponse resp, Object r) throws IOException {
    resp.setContentType("application/json;charset=utf-8");
    resp.setCharacterEncoding("UTF-8");
    DslJson<Object> json = JsonHelper.getJson();
    JsonWriter w = JsonHelper.getWriter();
    synchronized (w) {
      w.reset(resp.getOutputStream());
      json.serialize(w, r);
      w.flush();
    }
  }
}

