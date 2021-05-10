package net.pieroxy.conkw.webapp.servlets.auth;

import net.pieroxy.conkw.config.User;

import java.time.Duration;

public class ChallengeResponse {
  long created = System.currentTimeMillis();
  String saltValue;
  User user;

  public ChallengeResponse(String saltValue, User user) {
    this.saltValue = saltValue;
    this.user = user;
  }

  public boolean expired() {
    long now = System.currentTimeMillis();
    return now-created > Duration.ofSeconds(5).toMillis();
  }

  public String getSaltValue() {
    return saltValue;
  }

  public void setSaltValue(String saltValue) {
    this.saltValue = saltValue;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
