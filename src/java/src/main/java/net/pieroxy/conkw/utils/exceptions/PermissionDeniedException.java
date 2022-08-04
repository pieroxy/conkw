package net.pieroxy.conkw.utils.exceptions;

import net.pieroxy.conkw.api.metadata.ApiEndpoint;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;

public class PermissionDeniedException extends Exception {
  public PermissionDeniedException(ApiEndpoint endpoint, User user, UserRole roleNeeded) {
    super("Endpoint " + (endpoint==null ? "null" : endpoint.getClass().getSimpleName()) + " could not be accessed by " + (user == null ? "<anonymous>" : user.getId()) + " because it needs the role " + (roleNeeded == null ? "null":roleNeeded.name()));
  }
}
