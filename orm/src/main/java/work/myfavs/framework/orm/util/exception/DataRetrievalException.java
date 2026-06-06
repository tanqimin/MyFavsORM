package work.myfavs.framework.orm.util.exception;

/**
 * 数据检索异常，当执行 SQL 查询或更新操作失败时抛出。
 * <p>
 * 对应场景包括：创建 {@code PreparedStatement} 失败、执行 {@code executeQuery} / {@code
 * executeUpdate} / {@code executeBatch} 时发生 SQL 异常、设置 {@code fetchSize} 失败、
 * 绑定 SQL 参数失败等。
 * </p>
 *
 * @since 1.0.0
 * @author tanqimin
 */
public class DataRetrievalException extends DataAccessException {

  /**
   * 构造一个不带详细消息和原因的新异常。
   */
  public DataRetrievalException() {}

  /**
   * 构造一个带格式化消息的新异常。
   *
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public DataRetrievalException(String message, Object... params) {
    super(String.format(message, params));
  }

  /**
   * 构造一个带格式化消息和原因的新异常。
   *
   * @param cause   导致此异常的原因
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public DataRetrievalException(Throwable cause, String message, Object... params) {
    super(String.format(message, params), cause);
  }

  /**
   * 构造一个带原因的新异常。
   *
   * @param cause 导致此异常的原因
   */
  public DataRetrievalException(Throwable cause) {
    super(cause);
  }
}
