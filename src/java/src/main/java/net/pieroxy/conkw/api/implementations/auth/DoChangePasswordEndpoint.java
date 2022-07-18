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

@Endpoint(
    method = ApiMethod.POST,
    role = UserRole.ANONYMOUS
)
public class DoChangePasswordEndpoint extends AbstractApiEndpoint<DoChangePasswordInput, DoChangePasswordOutput> {
  private final Services services;

  public DoChangePasswordEndpoint(Services services) {
    this.services = services;
  }

  @Override
  public DoChangePasswordOutput process(DoChangePasswordInput input, User user) throws Exception {
    if (input.getA().trim().length()<8) {
      throw new DisplayMessageException("Passwords should be at least 8 characters.");
    }
    if (!input.getA().trim().equals(input.getB().trim())) {
      throw new DisplayMessageException("Both passwords provided don't match.");
    }
    UserLogin ul = services.getUserService().performAuthentication(input.getLogin(), input.getActual());
    if (ul!=null) {
      services.getUserService().changePassword(ul.getUser(), input.getA().trim());
      return new DoChangePasswordOutput();
    } else {
      throw new DisplayMessageException("The current password is incorrect.");
    }
  }
}

@TypeScriptType
@CompiledJson
class DoChangePasswordInput {
  private String login;
  private String actual;
  private String a;
  private String b;

  public String getActual() {
    return actual;
  }

  public void setActual(String actual) {
    this.actual = actual;
  }

  public String getA() {
    return a;
  }

  public void setA(String a) {
    this.a = a;
  }

  public String getB() {
    return b;
  }

  public void setB(String b) {
    this.b = b;
  }

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }
}
@TypeScriptType
@CompiledJson
class DoChangePasswordOutput {}