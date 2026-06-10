package work.myfavs.framework.orm.meta.pagination;

import java.io.Serializable;
import java.util.List;

/**
 * 排序接口，定义排序条件集合的获取方法。
 * <p>实现此接口的对象可作为排序参数传递到 {@code Sql.orderBy(ISortable)} 方法中。</p>
 *
 * @see Order
 * @see SortableCriteria
 * @see work.myfavs.framework.orm.meta.clause.Sql#orderBy(ISortable)
 */
public interface ISortable extends Serializable {
  /**
   * 获取排序条件集合
   *
   * @return 排序条件集合
   */
  List<Order> getOrderBy();
}
