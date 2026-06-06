package work.myfavs.framework.orm.util.exception;

/**
 * 分页查询异常，当分页查询参数不合法时抛出。
 * <p>
 * 对应场景包括：当前页码（{@code currentPage}）小于 1、每页记录数（{@code pageSize}）小于 1、
 * 每页记录数超出系统设置的最大值（{@code maxPageSize}）等。
 * </p>
 *
 * @since 1.0.0
 * @author tanqimin
 */
public class PaginationException extends InvalidDataAccessException {

  /**
   * 构造一个不带详细消息和原因的新异常。
   */
  public PaginationException() {}

  /**
   * 构造一个带格式化消息的新异常。
   *
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public PaginationException(String message, Object... params) {
    super(String.format(message, params));
  }

  /**
   * 构造一个带格式化消息和原因的新异常。
   *
   * @param cause   导致此异常的原因
   * @param message 异常消息模板，包含 {@link String#format(String, Object...)} 占位符
   * @param params  消息模板参数
   */
  public PaginationException(Throwable cause, String message, Object... params) {
    super(String.format(message, params), cause);
  }

  /**
   * 构造一个带原因的新异常。
   *
   * @param cause 导致此异常的原因
   */
  public PaginationException(Throwable cause) {
    super(cause);
  }
}
