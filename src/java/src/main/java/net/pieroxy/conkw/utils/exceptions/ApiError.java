package net.pieroxy.conkw.utils.exceptions;

public class ApiError extends Exception {
  public ApiError(String message) {
    super(message);
  }

  public ApiError(String message, Throwable cause) {
    super(message, cause);
  }
}
