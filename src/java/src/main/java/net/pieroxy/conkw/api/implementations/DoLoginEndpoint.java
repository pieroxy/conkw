package net.pieroxy.conkw.api.implementations;

import net.pieroxy.conkw.api.metadata.AbstractApiEndpoint;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

public class DoLoginEndpoint extends AbstractApiEndpoint<DoLoginEndpointInput, DoLoginEndpointOutput> {
  @Override
  public DoLoginEndpointOutput process(DoLoginEndpointInput input) throws Exception {
    return null;
  }

  @Override
  public Class<DoLoginEndpointInput> getType() {
    return DoLoginEndpointInput.class;
  }
}

@TypeScriptType
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
class DoLoginEndpointOutput {
  String token;
}