package net.pieroxy.conkw.webapp.servlets.auth;

import net.pieroxy.conkw.config.CredentialsHolder;

import java.time.Duration;

public class ChallengeResponse {
  long created = System.currentTimeMillis();
  String saltValue;
  CredentialsHolder credentialsHolder;

  public ChallengeResponse(String saltValue, CredentialsHolder user) {
    this.saltValue = saltValue;
    this.credentialsHolder = user;
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

  public CredentialsHolder getCredentialsHolder() {
    return credentialsHolder;
  }

  public void setCredentialsHolder(CredentialsHolder credentialsHolder) {
    this.credentialsHolder = credentialsHolder;
  }
}
