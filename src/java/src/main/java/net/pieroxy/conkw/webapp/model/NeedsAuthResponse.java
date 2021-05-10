package net.pieroxy.conkw.webapp.model;

import com.dslplatform.json.CompiledJson;

@CompiledJson(onUnknown = CompiledJson.Behavior.FAIL)
public class NeedsAuthResponse {
  private long timestamp;
  private boolean needsAuthentication;
  private String saltForPassword;
  private String sessionToken;
  private String errorMessage;

  public NeedsAuthResponse() {
    this.timestamp = System.currentTimeMillis();
    this.needsAuthentication = true;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isNeedsAuthentication() {
    return needsAuthentication;
  }

  public void setNeedsAuthentication(boolean needsAuthentication) {
    this.needsAuthentication = needsAuthentication;
  }

  public String getSaltForPassword() {
    return saltForPassword;
  }

  public void setSaltForPassword(String saltForPassword) {
    this.saltForPassword = saltForPassword;
  }

  public String getSessionToken() {
    return sessionToken;
  }

  public void setSessionToken(String sessionToken) {
    this.sessionToken = sessionToken;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
