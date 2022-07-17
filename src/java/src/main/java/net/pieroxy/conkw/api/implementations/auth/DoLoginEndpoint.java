package net.pieroxy.conkw.api.implementations.auth;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.api.model.User;
import net.pieroxy.conkw.api.model.UserLogin;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;

import java.util.logging.Logger;

@Endpoint(
    method = ApiMethod.POST,
    role = UserRole.ANONYMOUS
)
public class DoLoginEndpoint extends AbstractApiEndpoint<DoLoginEndpointInput, DoLoginEndpointOutput> {
  private final static Logger LOGGER = Logger.getLogger(DoLoginEndpoint.class.getName());

  private final Services services;

  public DoLoginEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public DoLoginEndpointOutput process(DoLoginEndpointInput input) throws Exception {
    LOGGER.info("Login request for user " + input.getLogin());
    UserLogin ul = services.getUserService().performAuthentication(input.getLogin(), input.getPassword());
    if (ul.getUser() != null) {
      String session = null;
      if (!ul.isMustChangePassword()) session = services.getUserSessionService().createSession(ul.getUser());
      return new DoLoginEndpointOutput(session, ul);
    } else {
      throw new DisplayMessageException("Username or password not recognized.");
    }
  }

  @Override
  public Class<DoLoginEndpointInput> getType() {
    return DoLoginEndpointInput.class;
  }
}

@TypeScriptType
@CompiledJson
class DoLoginEndpointInput {
  private String login;
  private String password;

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

@TypeScriptType
@CompiledJson
class DoLoginEndpointOutput {
  private String token;
  private User user;
  private boolean passwordMustChangeNow;

  public DoLoginEndpointOutput(String token, UserLogin user) {
    this.token = token;
    this.user = user.getUser();
    this.passwordMustChangeNow = user.isMustChangePassword();
  }

  public DoLoginEndpointOutput() {
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isPasswordMustChangeNow() {
    return passwordMustChangeNow;
  }

  public void setPasswordMustChangeNow(boolean passwordMustChangeNow) {
    this.passwordMustChangeNow = passwordMustChangeNow;
  }
}