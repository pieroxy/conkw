package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.utils.JsonHelper;
import net.pieroxy.conkw.utils.pools.hashmap.HashMapPool;
import net.pieroxy.conkw.webapp.Filter;
import net.pieroxy.conkw.webapp.grabbers.ExternalMetricsGrabber;
import net.pieroxy.conkw.webapp.model.EmiInput;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The external metrics ingestion endpoint
 */
public class Emi extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(Emi.class.getName());

  public static final String CONTENT_TYPE_JSON="application/json";

  private static Map<String, ExternalMetricsGrabber> allData = HashMapPool.getInstance().borrow(1);

  public static synchronized void addOrUpdateGrabber(ExternalMetricsGrabber externalMetricsGrabber) {
    Map tmp = allData;
    Map<String, ExternalMetricsGrabber> td = HashMapPool.getInstance().borrow(allData, 1);
    String name = externalMetricsGrabber.getName();
    td.put(name, externalMetricsGrabber);
    LOGGER.log(Level.INFO, "Registered EIG " + name);
    allData = td;
    HashMapPool.getInstance().giveBack(tmp);
  }

  public static synchronized void clearGrabbers() {
    Map tmp = allData;
    allData = HashMapPool.getInstance().borrow(1);
    HashMapPool.getInstance().giveBack(tmp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    req.setAttribute(Filter.API_VERB, "emi");
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
    if (ct.equals(CONTENT_TYPE_JSON))
      parseJsonInput(req, resp, mg);
    else
      parseSingleInput(req, resp, mg);

    respond(resp, 200, "OK\n");
  }

  private void parseJsonInput(HttpServletRequest req, HttpServletResponse resp, ExternalMetricsGrabber mg) {
    try (EmiInput data = JsonHelper.getJson().deserialize(EmiInput.class, req.getInputStream())) {
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
      resp.setContentType("text/html; charset=UTF-8");
      resp.setCharacterEncoding("UTF-8");
      PrintWriter w = resp.getWriter();
      w.println(s);
      w.flush();
      w.close();
    } catch (IOException e) {
      // Do we need to log anything if we cannot write the response back ?
      // It usually means the client has already disconnected, not really our problem.
    }
  }
}

