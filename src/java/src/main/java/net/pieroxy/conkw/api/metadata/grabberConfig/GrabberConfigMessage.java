package net.pieroxy.conkw.api.metadata.grabberConfig;

public class GrabberConfigMessage {
  boolean isError;
  String fieldName;
  String message;

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
