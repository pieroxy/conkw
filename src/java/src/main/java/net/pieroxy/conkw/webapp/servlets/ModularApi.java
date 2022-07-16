package net.pieroxy.conkw.webapp.servlets;

import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.reflection.GetAccessibleClasses;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Constructor;
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

  private final Services services;

  public ModularApi(Services services) {
    this.services = services;
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    initialize();
    String endpoint = req.getRequestURI();
    if (endpoint.startsWith("/api/")) endpoint = endpoint.substring(5) + "Endpoint";
    AbstractApiEndpoint ep = endpoints.get(endpoint);
    if (LOGGER.isLoggable(Level.FINE)) LOGGER.fine("POST API called - " + endpoint + " " + ep);
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
      if (LOGGER.isLoggable(Level.FINE)) LOGGER.fine("POST API called - " + endpoint + " " + ep);
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
          LOGGER.fine("Testing class " + c.getName() + " " + c.isInstance(AbstractApiEndpoint.class) + " " + c.isAnnotationPresent(Endpoint.class));
          if (AbstractApiEndpoint.class.isAssignableFrom(c) && c.isAnnotationPresent(Endpoint.class)) {
            LOGGER.fine("Found class " + c.getName());

            Constructor[]cs = c.getConstructors();
            for (Constructor constructor : cs) {
              if (constructor.getParameterCount() == 0) {
                newEp.put(c.getSimpleName(), (AbstractApiEndpoint)c.newInstance());
                break;
              }
              if (constructor.getParameterCount()==1 && constructor.getParameters()[0].getType()==Services.class) {
                newEp.put(c.getSimpleName(), (AbstractApiEndpoint)constructor.newInstance(services));
                break;
              }
            }
            if (!newEp.containsKey(c.getSimpleName())) {
              throw new RuntimeException("Could not instanciate endpoint " + c.getName());
            }
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
