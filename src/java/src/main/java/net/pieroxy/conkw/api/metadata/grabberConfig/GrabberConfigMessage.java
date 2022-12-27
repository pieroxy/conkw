package net.pieroxy.conkw.api.metadata.grabberConfig;

import com.dslplatform.json.CompiledJson;
import net.pieroxy.conkw.api.metadata.TypeScriptType;

@CompiledJson
@TypeScriptType
public class GrabberConfigMessage {
  boolean isError;
  String fieldName;
  String message;

  public GrabberConfigMessage() {
  }

  public GrabberConfigMessage(boolean isError, String fieldName, String message) {
    this.isError = isError;
    this.fieldName = fieldName;
    this.message = message;
  }

  public boolean isError() {
    return isError;
  }

  public void setError(boolean error) {
    isError = error;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
