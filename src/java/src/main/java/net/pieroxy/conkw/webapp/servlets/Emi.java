package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.webapp.grabbers.ExternalMetricsGrabber;
import net.pieroxy.conkw.webapp.model.EmiInput;
import net.pieroxy.conkw.webapp.model.Response;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The external metrics ingestion endpoint
 */
public class Emi extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(Emi.class.getName());

  public static final String CONTENT_TYPE="application/json";

  static Map<String, ExternalMetricsGrabber> allData = new HashMap<>();

  public static synchronized void addOrUpdateGrabber(ExternalMetricsGrabber externalMetricsGrabber) {
    Map<String, ExternalMetricsGrabber> td = new HashMap<>();
    td.putAll(allData);
    String name = externalMetricsGrabber.getName();
    td.put(name, externalMetricsGrabber);
    LOGGER.log(Level.INFO, "Registered EIG " + name);
    allData = td;
  }

  public static synchronized void clearGrabbers() {
    allData = new HashMap<>();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String ns = req.getParameter("ns");
    if (ns == null) {
      respond(resp, 400, "No namespace provided.");
      return;
    }
    ExternalMetricsGrabber mg = allData.get(ns);
    if (mg == null) {
      respond(resp, 400, "Provided namespace ("+ns+") doesn't exist.");
      return;
    }

    String ct = req.getContentType();
    if (ct==null) ct="";
    if (ct.equals(CONTENT_TYPE))
      parseJsonInput(req, resp, mg);
    else
      parseSingleInput(req, resp, mg);

    respond(resp, 200, "OK\n");
  }

  private void parseJsonInput(HttpServletRequest req, HttpServletResponse resp, ExternalMetricsGrabber mg) {
    try {
      EmiInput data = JsonHelper.getJson().deserialize(EmiInput.class, req.getInputStream());
      data.getNum().entrySet().forEach(e -> mg.addMetric(e.getKey(), e.getValue()));
      data.getStr().entrySet().forEach(e -> mg.addMetric(e.getKey(), e.getValue()));
    } catch (IOException e) {
      respond(resp, 500, "Error while reading the JSON: " + e.getMessage());
    }
  }

  private void parseSingleInput(HttpServletRequest req, HttpServletResponse resp, ExternalMetricsGrabber mg) {
    String name = req.getParameter("name");
    String value = req.getParameter("value");
    String type = req.getParameter("type");
    if (name == null) {
      respond(resp, 400, "No name provided.");
      return;
    }
    if (value == null) {
      respond(resp, 400, "No value provided.");
      return;
    }
    if (type == null) {
      respond(resp, 400, "No type provided.");
      return;
    }
    switch (type) {
      case "str":
        mg.addMetric(name, value);
        break;
      case "num":
        double d;
        try {
          d = Double.parseDouble(value);
        } catch (Exception e) {
          respond(resp, 400, "Unparseable numeric value provided.");
          return;
        }
        mg.addMetric(name, d);
        break;
      default:
        respond(resp, 400, "Unknown type provided.");
        return;
    }
  }

  private void respond(HttpServletResponse resp, int i, String s) {
    resp.setStatus(i);
    try {
      resp.setContentType("text/plain");
      resp.getWriter().println(s);
    } catch (IOException e) {
      // Do we need to log anything if we cannot write the response back ?
      // It usually means the client has already disconnected, not really our problem.
    }
  }
}

