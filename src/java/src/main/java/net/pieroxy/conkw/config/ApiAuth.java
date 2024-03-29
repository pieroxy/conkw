package net.pieroxy.conkw.config;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.utils.duration.CDuration;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class ApiAuth {
  private boolean auth;
  private CDuration sessionLifetime;
  private CDuration sessionInactivityTimeout;
  private CredentialsHolder[] users;

  public boolean isAuth() {
    return auth;
  }

  public void setAuth(boolean auth) {
    this.auth = auth;
  }

  public CredentialsHolder[] getUsers() {
    return users;
  }

  public void setUsers(CredentialsHolder[] users) {
    this.users = users;
  }

  public CDuration getSessionLifetime() {
    return sessionLifetime;
  }

  public void setSessionLifetime(CDuration sessionLifetime) {
    this.sessionLifetime = sessionLifetime;
  }

  public CDuration getSessionInactivityTimeout() {
    return sessionInactivityTimeout;
  }

  public void setSessionInactivityTimeout(CDuration sessionInactivityTimeout) {
    this.sessionInactivityTimeout = sessionInactivityTimeout;
  }
}
