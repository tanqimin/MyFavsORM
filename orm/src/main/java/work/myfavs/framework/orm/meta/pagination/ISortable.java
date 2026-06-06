package work.myfavs.framework.orm.meta.pagination;

import java.io.Serializable;
import java.util.List;

public interface ISortable extends Serializable {
  List<Order> getOrderBy();
}
