package net.pieroxy.conkw.config;

import java.util.Objects;

public class User {
  String login;
  String password;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return login.equals(user.login) && password.equals(user.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(login, password);
  }
}
