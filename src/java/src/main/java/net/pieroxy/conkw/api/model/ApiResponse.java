package net.pieroxy.conkw.api.model;

public class ApiResponse<T> {
  private String code;
  private String message;
  private T content;

  public static <T> ApiResponse<T> buildOkResult(T content) {
    ApiResponse<T> res = new ApiResponse();
    res.setCode(ApiResultCodes.OK);
    res.setContent(content);
    return res;
  }

  public static <T> ApiResponse<T> buildErrResult(ApiResultCodes code, String message) {
    ApiResponse<T> res = new ApiResponse();
    res.setCode(code);
    res.setMessage(message);
    return res;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setCode(ApiResultCodes code) {
    this.code = code.name();
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getContent() {
    return content;
  }

  public void setContent(T content) {
    this.content = content;
  }
}
