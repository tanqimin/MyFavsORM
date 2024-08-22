package work.myfavs.framework.orm.meta.pagination;

import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.io.Serializable;

/**
 * 排序
 */
public class Order implements Serializable {

  private String field;
  private String direction;

  /**
   * 获取排序字段
   *
   * @return 排序字段
   */
  public String getField() {
    return field;
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
   * 设置排序字段
   *
   * @param field 排序字段
   */
  public void setField(String field) {
    this.field = field;
  }

  /**
   * 设置排序方向
   *
   * @param direction 排序方向
   */
  public void setDirection(String direction) {
    this.direction = direction;
  }

  @Override
  public String toString() {
    if (StringUtil.isBlank(field)) {
      throw new DBException("排序字段不能为空！");
    }

    if (isAscending())
      return this.field;

    if (!StringUtil.equalsIgnoreCase(direction, "DESC")) {
      throw new DBException("排序方式必须为 ASC 或 DESC！");
    }
    return String.format("%s DESC", this.field);
  }

  public boolean isAscending() {
    return StringUtil.isEmpty(this.direction)
        || StringUtil.equalsIgnoreCase(this.direction, "ASC");
  }
}
