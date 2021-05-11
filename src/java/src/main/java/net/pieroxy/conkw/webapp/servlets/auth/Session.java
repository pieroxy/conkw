package net.pieroxy.conkw.webapp.servlets.auth;

import net.pieroxy.conkw.config.ApiAuth;
import net.pieroxy.conkw.config.User;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;

import java.time.Duration;

public class Session {
  long created = System.currentTimeMillis();
  long lastAccessed = System.currentTimeMillis();
  CDuration sessionLifetime;
  CDuration sessionInactiveTimeout;
  String key;
  User user;

  public Session(String key, User user, ApiAuth config)
  {
    this.key = key;
    this.user = user;
    applyConfig(config);
  }

  public void applyConfig(ApiAuth config) {
    if (config == null) config = new ApiAuth();
    sessionLifetime = CDurationParser.getOrDefault(config.getSessionLifetime(), "1y");
    sessionInactiveTimeout = CDurationParser.getOrDefault(config.getSessionInactivityTimeout(), "7d");
  }

  public void use() {
    lastAccessed = System.currentTimeMillis();
  }

  public boolean expired() {
    long now = System.currentTimeMillis();
    return sessionInactiveTimeout.isExpired(lastAccessed, now) ||
        sessionLifetime.isExpired(created, now);
  }

  public long getCreated() {
    return created;
  }

  public void setCreated(long created) {
    this.created = created;
  }

  public long getLastAccessed() {
    return lastAccessed;
  }

  public void setLastAccessed(long lastAccessed) {
    this.lastAccessed = lastAccessed;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
