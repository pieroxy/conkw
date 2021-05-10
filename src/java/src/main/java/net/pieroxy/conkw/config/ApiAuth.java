package net.pieroxy.conkw.config;

public class ApiAuth {
  private boolean auth;
  private User[] users;

  public boolean isAuth() {
    return auth;
  }

  public void setAuth(boolean auth) {
    this.auth = auth;
  }

  public User[] getUsers() {
    return users;
  }

  public void setUsers(User[] users) {
    this.users = users;
  }
}
