package net.pieroxy.conkw.webapp.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class ModularApi extends HttpServlet {
  private final static Logger LOGGER = Logger.getLogger(ModularApi.class.getName());

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json");
    resp.getOutputStream().write("{\"code\":\"OK\", \"message\":\"placeholder\"}".getBytes(StandardCharsets.UTF_8));
  }
}
