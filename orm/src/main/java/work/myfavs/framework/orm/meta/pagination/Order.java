package work.myfavs.framework.orm.meta.pagination;

import work.myfavs.framework.orm.meta.criteria.SortableCriteria;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import java.io.Serializable;

import static work.myfavs.framework.orm.util.common.SqlUtil.checkInjection;
import static work.myfavs.framework.orm.util.common.StringUtil.*;

/**
 * 排序对象，封装排序字段和排序方向。
 * <p>支持通过 {@link #parse(String)} 方法从字符串解析，格式为 {@code "fieldName ASC"} 或 {@code "fieldName DESC"}。
 * 默认升序。字段名经过 SQL 注入检查。</p>
 *
 * @see SortableCriteria
 * @see work.myfavs.framework.orm.meta.clause.Sql#orderBy(String)
 */
public class Order implements Serializable {

  /**
   * 排序字段
   */
  private String field;
  /**
   * 排序方向
   */
  private String direction;

  /**
   * 构造排序对象
   */
  public Order() {
  }

  /**
   * 构造排序对象
   *
   * @param field     排序字段
   * @param direction 排序方向（ASC 或 DESC）
   */
  public Order(String field, String direction) {
    this.field = field;
    this.direction = direction;
  }

  /**
   * 获取排序字段
   *
   * @return 排序字段
   */
  public String getField() {
    return field;
  }

  /**
   * 设置排序字段
   *
   * @param field 排序字段
   */
  public void setField(String field) {
    this.field = field;
  }

  /**
   * 获取排序方向
   *
   * @return 排序方向
   */
  public String getDirection() {
    return direction;
  }

  /**
   * 设置排序方向
   *
   * @param direction 排序方向（ASC 或 DESC）
   */
  public void setDirection(String direction) {
    this.direction = direction;
  }

  /**
   * 解析排序字符串
   *
   * @param orderBy 排序字符串
   * @return {@link Order}
   */
  public static Order parse(String orderBy) {
    if (isBlank(orderBy))
      throw new InvalidDataAccessException("排序字段不能为空！");

    String[] split = trim(orderBy).split(Constant.SPACE);

    if (split.length > 2)
      throw new InvalidDataAccessException(String.format("错误的排序格式: %s", orderBy));

    if (split.length == 1) {
      return new Order(trim(split[0]), "ASC");
    }

    return new Order(trim(split[0]), trim(split[1]));
  }

  /**
   * 获取排序语句
   * <pre>
   *   {@code {field}}
   *   或
   *   {@code {field} DESC}
   * </pre>
   *
   * @return 排序语句
   */
  public String getClause() {
    if (isBlank(this.field))
      throw new InvalidDataAccessException("排序字段不能为空！");

    String orderByField = checkInjection(this.field);

    if (isAscending())
      return orderByField;

    if (equalsIgnoreCase(direction, "DESC"))
      return orderByField.concat(" DESC");

    throw new InvalidDataAccessException("排序方向必须为 ASC 或 DESC！");
  }

  /**
   * 当前排序方向是否为升序
   *
   * @return 升序返回 {@code true}
   */
  public boolean isAscending() {
    return isEmpty(this.direction)
        || equalsIgnoreCase(this.direction, "ASC");
  }
}
