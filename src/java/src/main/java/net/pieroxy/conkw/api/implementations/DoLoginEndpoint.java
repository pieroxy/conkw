package net.pieroxy.conkw.api.implementations;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.config.Credentials;
import net.pieroxy.conkw.config.CredentialsStore;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;

@Endpoint(
    method = ApiMethod.POST,
    role = UserRole.ANONYMOUS
)
public class DoLoginEndpoint extends AbstractApiEndpoint<DoLoginEndpointInput, DoLoginEndpointOutput> {
  private static CredentialsStore credentials;

  @Override
  public DoLoginEndpointOutput process(DoLoginEndpointInput input) throws Exception {
    for (Credentials c : credentials.getStore().values()) {
      if (c.getId().equals(input.getLogin())) {
        return authOk(c, input);
      }
    }
    throw new DisplayMessageException("Username or password not recognized.");
  }

  private DoLoginEndpointOutput authOk(Credentials c, DoLoginEndpointInput input) throws DisplayMessageException {
    if (!StringUtil.isNullOrEmptyTrimmed(c.getSecret())) {
      // Simple password check
      if (input.getPassword().equals(c.getSecret())) {
        return buildSession(c);
      }
    }
    throw new DisplayMessageException("Username or password not recognized.");
  }

  private DoLoginEndpointOutput buildSession(Credentials c) {
    return new DoLoginEndpointOutput(c.getId());
  }

  @Override
  public Class<DoLoginEndpointInput> getType() {
    return DoLoginEndpointInput.class;
  }

  public static void setCredentials(CredentialsStore store) {
    credentials = store;
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
  String token;

  public DoLoginEndpointOutput(String token) {
    this.token = token;
  }

  public DoLoginEndpointOutput() {
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}