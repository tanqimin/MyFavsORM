package work.myfavs.framework.orm.meta.criteria;

import work.myfavs.framework.orm.meta.pagination.ISortable;
import work.myfavs.framework.orm.meta.pagination.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 排序查询条件基类，实现 {@link ISortable} 接口。
 * <p>持有 {@link Order} 列表，可通过 setter 设置排序条件。
 * {@link PageableCriteria} 继承此类，同时获得排序和分页能力。</p>
 *
 * @see Order
 * @see PageableCriteria
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

  /**
   * 设置排序条件集合
   *
   * @param orderBy 排序条件集合
   */
  public void setOrderBy(List<Order> orderBy) {
    this.orderBy = orderBy;
  }
}
