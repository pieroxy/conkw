package net.pieroxy.conkw.webapp.servlets.auth;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.config.ApiAuth;
import net.pieroxy.conkw.config.User;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class Session {
  private long created = System.currentTimeMillis();
  private long lastAccessed = System.currentTimeMillis();
  private transient CDuration sessionLifetime;
  private transient CDuration sessionInactiveTimeout;
  private String key;
  private User user;

  public Session() {
  }

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
