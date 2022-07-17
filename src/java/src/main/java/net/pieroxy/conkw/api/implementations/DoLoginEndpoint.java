package net.pieroxy.conkw.api.implementations;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.ApiMethod;
import net.pieroxy.conkw.api.metadata.Endpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.config.Credentials;
import net.pieroxy.conkw.config.CredentialsHolder;
import net.pieroxy.conkw.config.HashedSecret;
import net.pieroxy.conkw.config.UserRole;
import net.pieroxy.conkw.utils.Services;
import net.pieroxy.conkw.utils.StringUtil;
import net.pieroxy.conkw.utils.exceptions.DisplayMessageException;
import net.pieroxy.conkw.utils.hashing.HashTools;
import net.pieroxy.conkw.webapp.servlets.auth.Session;

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
    for (Credentials c : services.getCredentialsStore().getStore().values()) {
      if (c.getId().equals(input.getLogin()) && c.getRoles()!=null && c.getRoles().size()>0) {
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
    } else if (c.getHashedSecret()!=null) {
      HashedSecret hs = c.getHashedSecret();
      String hashed = HashTools.hashPassword(input.getPassword(), hs);
      if (hashed.equals(hs.getHashedSecret())) {
        return buildSession(c);
      }
    }
    throw new DisplayMessageException("Username or password not recognized.");
  }

  private DoLoginEndpointOutput buildSession(Credentials c) {
    Session s = services.getApiAuthManager().buildSession(new CredentialsHolder(c));
    return new DoLoginEndpointOutput(s.getKey());
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