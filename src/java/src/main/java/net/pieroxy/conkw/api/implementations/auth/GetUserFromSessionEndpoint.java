package net.pieroxy.conkw.api.implementations.auth;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.services.UserSessionService;
import net.pieroxy.conkw.utils.Services;

import java.util.logging.Logger;

@Endpoint(
    method = ApiMethod.GET,
    role = UserRole.ANONYMOUS
)
public class GetUserFromSessionEndpoint extends AbstractApiEndpoint<GetUserFromSessionInput, GetUserFromSessionOutput> {
  private final static Logger LOGGER = Logger.getLogger(GetUserFromSessionEndpoint.class.getName());
  private final Services services;

  public GetUserFromSessionEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public GetUserFromSessionOutput process(GetUserFromSessionInput input) throws Exception {
    UserSessionService.Session session = services.getUserSessionService().getSession(input.getToken());
    if (session!=null) {
      LOGGER.info("Found user " + session.getUserid());
      return new GetUserFromSessionOutput(services.getUserService().getUserById(session.getUserid()));
    } else {
      LOGGER.info("User not found for session " + input.getToken());
      return new GetUserFromSessionOutput();
    }
  }

  @Override
  public Class<GetUserFromSessionInput> getType() {
    return GetUserFromSessionInput.class;
  }
}

@TypeScriptType
@CompiledJson
class GetUserFromSessionInput {
  String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}

@TypeScriptType
@CompiledJson
class GetUserFromSessionOutput {
  private User user;
  private boolean invalidSession;

  public GetUserFromSessionOutput(User user) {
    this.user = user;
  }

  public GetUserFromSessionOutput() {
    invalidSession = true;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isInvalidSession() {
    return invalidSession;
  }

  public void setInvalidSession(boolean invalidSession) {
    this.invalidSession = invalidSession;
  }
}