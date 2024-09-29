package work.myfavs.framework.orm.meta.pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.exception.DBException;

import java.io.Serializable;

import static work.myfavs.framework.orm.util.common.SqlUtil.checkInjection;
import static work.myfavs.framework.orm.util.common.StringUtil.*;

/**
 * 排序
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
   * 解析排序字符串
   *
   * @param orderBy 排序字符串
   * @return {@link Order}
   */
  public static Order parse(String orderBy) {
    if (isBlank(orderBy))
      throw new DBException("排序字段不能为空！");

    String[] split = orderBy.split(Constant.SPACE);

    if (split.length > 2)
      throw new DBException(String.format("错误的排序格式: %s", orderBy));

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
      throw new DBException("排序字段不能为空！");

    String orderByField = checkInjection(this.field);

    if (isAscending())
      return orderByField;

    if (equalsIgnoreCase(direction, "DESC"))
      return orderByField.concat(" DESC");

    throw new DBException("排序方向必须为 ASC 或 DESC！");
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
