package work.myfavs.framework.sb2.demo.common.exception;

/**
 * 统一错误响应体，供全局异常处理器输出.
 */
public class ErrorResponse {

  private int    code;
  private String message;

  public ErrorResponse() {
  }

  public ErrorResponse(int code, String message) {
    this.code    = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
