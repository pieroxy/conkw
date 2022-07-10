package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.utils.reflection.GetAccessibleClasses;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModularApi extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(ModularApi.class.getName());

  Map<String, AbstractApiEndpoint> endpoints;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    initialize();
    String endpoint = req.getRequestURI();
    if (endpoint.startsWith("/api/")) endpoint = endpoint.substring(5) + "Endpoint";
    AbstractApiEndpoint ep = endpoints.get(endpoint);
    if (ep == null) {
      LOGGER.severe("Endpoint " + endpoint + " not found");
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    Endpoint parameters = ep.getClass().getAnnotation(Endpoint.class);
    if (parameters.method() != ApiMethod.POST) {
      LOGGER.severe("Endpoint " + endpoint + " nos accessible through POST");
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
      return;
    }
    try {
      ep.process(req,resp);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Endpoint " + endpoint + " threw an exception", e);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    initialize();
    resp.setContentType("application/json");
    resp.getOutputStream().write("{\"code\":\"TECH_ERROR\", \"message\":\"Not implemented yet\"}".getBytes(StandardCharsets.UTF_8));
  }


  private void initialize() {
    if (endpoints==null) {
      Map<String, AbstractApiEndpoint> newEp = new HashMap<>();
      try {
        List<Class<?>> allclasses = GetAccessibleClasses.getClasses("net.pieroxy.conkw");
        for (Class c : allclasses) {
          LOGGER.info("Testing class " + c.getName() + " " + c.isInstance(AbstractApiEndpoint.class) + " " + c.isAnnotationPresent(Endpoint.class));
          if (AbstractApiEndpoint.class.isAssignableFrom(c) && c.isAnnotationPresent(Endpoint.class)) {
            LOGGER.info("Found class " + c.getName());
            newEp.put(c.getSimpleName(), (AbstractApiEndpoint)c.newInstance());
          }
        }
        endpoints = newEp;
        LOGGER.info("Endpoints found: " + newEp.keySet().stream().collect(Collectors.joining(",")));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
