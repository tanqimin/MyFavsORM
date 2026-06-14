package work.myfavs.framework.sb2.demo.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 全局异常处理器，统一返回 {@link ErrorResponse} 格式的错误信息.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * 处理框架异常（DBException 及其子类）.
   *
   * @param ex 异常
   * @return 500 错误响应
   */
  @ExceptionHandler(DBException.class)
  public ResponseEntity<ErrorResponse> handleOrmException(DBException ex) {
    log.error("ORM 异常: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(500, ex.getMessage()));
  }

  /**
   * 处理通用异常.
   *
   * @param ex 异常
   * @return 500 错误响应
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
    log.error("未预期异常: {}", ex.getMessage(), ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponse(500, "服务器内部错误"));
  }
}
