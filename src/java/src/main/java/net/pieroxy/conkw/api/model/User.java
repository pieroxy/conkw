package net.pieroxy.conkw.api.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;
import net.pieroxy.conkw.config.UserRole;

@TypeScriptType
@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class User {
  private String id;
  private String readableName;
  private String emailAddress;
  private UserRole role;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getReadableName() {
    return readableName;
  }

  public void setReadableName(String readableName) {
    this.readableName = readableName;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }
}
