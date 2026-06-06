package work.myfavs.framework.orm.meta.criteria;

import work.myfavs.framework.orm.meta.pagination.ISortable;
import work.myfavs.framework.orm.meta.pagination.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序查询条件基类
 */
public abstract class SortableCriteria implements ISortable {
  private List<Order> orderBy = new ArrayList<>();

  /**
   * 获取排序集合
   *
   * @return 排序集合
   */
  @Override
  public List<Order> getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(List<Order> orderBy) {
    this.orderBy = orderBy;
  }
}
