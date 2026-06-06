package work.myfavs.framework.orm.util.exception;

/**
 * 数据库连接异常，当获取数据库连接、设置保存点、提交或回滚事务失败时抛出。
 * <p>
 * 对应场景包括：从 {@code DataSource} 获取连接失败、事务提交/回滚失败、
 * {@code Savepoint} 操作失败、连接释放或关闭失败等。
 * </p>
 *
 * @since 1.0.0
 * @author tanqimin
 */
public class ConnectionException extends DataAccessException {

  /**
   * 构造一个不带详细消息和原因的新异常。
   */
  public ConnectionException() {}

  /**
   * 构造一个带格式化消息的新异常。
   *
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public ConnectionException(String message, Object... params) {
    super(String.format(message, params));
  }

  /**
   * 构造一个带格式化消息和原因的新异常。
   *
   * @param cause   导致此异常的原因
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public ConnectionException(Throwable cause, String message, Object... params) {
    super(String.format(message, params), cause);
  }

  /**
   * 构造一个带原因的新异常。
   *
   * @param cause 导致此异常的原因
   */
  public ConnectionException(Throwable cause) {
    super(cause);
  }
}
