package work.myfavs.framework.orm.meta.pagination;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 分页基类
 *
 * @author tanqimin
 */
@Getter
@Setter
public abstract class PageBase<TModel> implements Serializable {

  private List<TModel> data = new ArrayList<>();
  private long         currentPage;
  private long         pageSize;

  protected <TOther> List<TOther> convertData(Function<TModel, TOther> fun) {

    List<TOther> list = new ArrayList<>();
    for (TModel item : this.getData()) {
      TOther apply = fun.apply(item);
      list.add(apply);
    }
    return list;
  }
}
