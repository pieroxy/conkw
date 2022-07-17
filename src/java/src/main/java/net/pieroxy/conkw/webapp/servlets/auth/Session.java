package net.pieroxy.conkw.webapp.servlets.auth;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.config.ApiAuth;
import net.pieroxy.conkw.config.CredentialsHolder;
import net.pieroxy.conkw.utils.duration.CDuration;
import net.pieroxy.conkw.utils.duration.CDurationParser;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class Session {
  private long created = System.currentTimeMillis();
  private long lastAccessed = System.currentTimeMillis();
  private transient CDuration sessionLifetime;
  private transient CDuration sessionInactiveTimeout;
  private String key;
  private CredentialsHolder credentialsHolder;

  public Session() {
  }

  public Session(String key, CredentialsHolder user, ApiAuth config)
  {
    this.key = key;
    this.credentialsHolder = user;
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

  public CredentialsHolder getCredentialsHolder() {
    return credentialsHolder;
  }

  public void setCredentialsHolder(CredentialsHolder credentialsHolder) {
    this.credentialsHolder = credentialsHolder;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
