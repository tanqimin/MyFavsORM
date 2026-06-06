package work.myfavs.framework.orm.util.exception;

/**
 * 数据库操作异常，ORM 框架所有数据访问异常的基类。
 * <p>
 * 继承 {@link RuntimeException}，属于非受检异常。
 * 框架中所有数据访问相关异常都应继承此类，以便调用方通过单一 {@code catch (DBException e)}
 * 统一处理。
 * </p>
 *
 * @since 1.0.0
 * @author tanqimin
 */
public class DBException extends RuntimeException {

  /**
   * 构造一个不带详细消息和原因的新异常。
   */
  public DBException() {}

  /**
   * 构造一个带格式化消息的新异常。
   *
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public DBException(String message, Object... params) {
    super(String.format(message, params));
  }

  /**
   * 构造一个带格式化消息和原因的新异常。
   *
   * @param cause   导致此异常的原因
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public DBException(Throwable cause, String message, Object... params) {
    super(String.format(message, params), cause);
  }

  /**
   * 构造一个带原因的新异常。
   *
   * @param cause 导致此异常的原因
   */
  public DBException(Throwable cause) {
    super(cause);
  }
}
