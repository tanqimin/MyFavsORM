package work.myfavs.framework.orm.meta.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

abstract class PageBase<TModel>
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

}
