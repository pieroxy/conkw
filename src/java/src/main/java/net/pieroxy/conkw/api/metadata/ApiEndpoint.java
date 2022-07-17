package net.pieroxy.conkw.api.metadata;

import net.pieroxy.conkw.api.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ApiEndpoint {
  void process(HttpServletRequest req, HttpServletResponse res, User user);
}
