package net.pieroxy.conkw.webapp.servlets.auth;

import net.pieroxy.conkw.config.User;

import java.time.Duration;

public class Session {
  long created = System.currentTimeMillis();
  long lastAccessed = System.currentTimeMillis();
  String key;
  User user;

  public Session(String key, User user) {
    this.key = key;
    this.user = user;
  }

  public void use() {
    lastAccessed = System.currentTimeMillis();
  }

  public boolean expired() {
    long now = System.currentTimeMillis();
    return now-lastAccessed > Duration.ofSeconds(60).toMillis();
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
