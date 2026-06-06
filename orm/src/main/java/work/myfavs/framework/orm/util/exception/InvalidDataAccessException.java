package work.myfavs.framework.orm.util.exception;

/**
 * 无效数据访问异常，当数据访问参数、配置或状态不合法时抛出。
 * <p>
 * 对应场景包括：实体缺少 {@code @Table} / {@code @PrimaryKey} 注解、数据库类型不支持、
 * 反射访问字段失败、类型转换错误、SQL 注入检查失败、参数索引重复等。
 * </p>
 *
 * @since 1.0.0
 * @author tanqimin
 */
public class InvalidDataAccessException extends DBException {

  /**
   * 构造一个不带详细消息和原因的新异常。
   */
  public InvalidDataAccessException() {}

  /**
   * 构造一个带格式化消息的新异常。
   *
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public InvalidDataAccessException(String message, Object... params) {
    super(String.format(message, params));
  }

  /**
   * 构造一个带格式化消息和原因的新异常。
   *
   * @param cause   导致此异常的原因
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public InvalidDataAccessException(Throwable cause, String message, Object... params) {
    super(String.format(message, params), cause);
  }

  /**
   * 构造一个带原因的新异常。
   *
   * @param cause 导致此异常的原因
   */
  public InvalidDataAccessException(Throwable cause) {
    super(cause);
  }
}
