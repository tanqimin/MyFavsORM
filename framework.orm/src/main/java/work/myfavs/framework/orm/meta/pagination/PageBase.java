package work.myfavs.framework.orm.meta.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class PageBase<TModel>
    implements Serializable {

  private List<TModel> data        = new ArrayList<>();
  private long         currentPage = 1;
  private long         pageSize    = 20;

  public List<TModel> getData() {

    return data;
  }

  public void setData(List<TModel> data) {

    this.data = data;
  }

  public long getCurrentPage() {

    return currentPage;
  }

  public void setCurrentPage(long currentPage) {

    this.currentPage = currentPage;
  }

  public long getPageSize() {

    return pageSize;
  }

  public void setPageSize(long pageSize) {

    this.pageSize = pageSize;
  }

  protected <TOther> List<TOther> convertData(Function<TModel, TOther> fun) {

    List<TOther> list = new ArrayList<>();
    for (Iterator<TModel> iterator = this.getData().iterator();
         iterator.hasNext(); ) {
      TModel item  = iterator.next();
      TOther apply = fun.apply(item);
      list.add(apply);
    }
    return list;
  }

}
