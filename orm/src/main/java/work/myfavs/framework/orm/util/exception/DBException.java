package work.myfavs.framework.orm.util.exception;

/**
 * 数据库操作异常类，继承 {@link RuntimeException}，用于封装 ORM 框架中的数据库相关异常
 */
public class DBException extends RuntimeException {

  /**
   * 构造空异常
   */
  public DBException() {}

  /**
   * 构造带格式化消息的异常
   *
   * @param message 异常消息模板
   * @param params  消息模板参数
   */
  public DBException(String message, Object... params) {

    super(String.format(message, params));
  }

  /**
   * 构造带格式化消息和原因的异常
   *
   * @param cause   原始异常
   * @param message 异常消息模板
   * @param params  消息模板参数
   */
  public DBException(Throwable cause, String message, Object... params) {

    super(String.format(message, params), cause);
  }

  /**
   * 构造带原因的异常
   *
   * @param cause 原始异常
   */
  public DBException(Throwable cause) {

    super(cause);
  }
}
