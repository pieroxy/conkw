package net.pieroxy.conkw.api.model;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@TypeScriptType
@CompiledJson(onUnknown = CompiledJson.Behavior.IGNORE)
public class User {
  String id;
  String readableName;
  String emailAddress;

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
}
