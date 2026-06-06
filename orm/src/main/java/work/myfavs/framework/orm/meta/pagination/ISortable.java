package work.myfavs.framework.orm.meta.pagination;

import java.io.Serializable;
import java.util.List;

/**
 * 排序接口，定义排序所需的操作方法
 */
public interface ISortable extends Serializable {
  /**
   * 获取排序条件集合
   *
   * @return 排序条件集合
   */
  List<Order> getOrderBy();
}
