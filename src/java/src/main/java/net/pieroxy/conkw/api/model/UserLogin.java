package net.pieroxy.conkw.api.model;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class UserLogin {
  private User user;
  private boolean mustChangePassword;

  public UserLogin(User user, boolean mustChangePassword) {
    this.user = user;
    this.mustChangePassword = mustChangePassword;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isMustChangePassword() {
    return mustChangePassword;
  }

  public void setMustChangePassword(boolean mustChangePassword) {
    this.mustChangePassword = mustChangePassword;
  }
}
