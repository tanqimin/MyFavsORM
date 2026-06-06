package work.myfavs.framework.orm.util.exception;

/**
 * 数据访问异常基类，ORM 框架中所有数据访问异常的根类型。
 * <p>
 * 继承 {@link RuntimeException}，属于非受检异常，使调用方无需显式捕获。
 * 框架中所有数据访问相关异常都应继承此类，以便调用方通过单一 catch 块统一处理。
 * </p>
 *
 * @since 1.0.0
 * @author tanqimin
 */
public class DataAccessException extends RuntimeException {

  /**
   * 构造一个不带详细消息和原因的新异常。
   */
  public DataAccessException() {}

  /**
   * 构造一个带格式化消息的新异常。
   *
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public DataAccessException(String message, Object... params) {
    super(String.format(message, params));
  }

  /**
   * 构造一个带格式化消息和原因的新异常。
   *
   * @param cause   导致此异常的原因
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public DataAccessException(Throwable cause, String message, Object... params) {
    super(String.format(message, params), cause);
  }

  /**
   * 构造一个带原因的新异常。
   *
   * @param cause 导致此异常的原因
   */
  public DataAccessException(Throwable cause) {
    super(cause);
  }
}
