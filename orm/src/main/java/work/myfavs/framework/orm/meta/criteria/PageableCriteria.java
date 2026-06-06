package work.myfavs.framework.orm.meta.criteria;

import work.myfavs.framework.orm.meta.pagination.IPageable;

/**
 * 分页查询条件基类
 */
public abstract class PageableCriteria extends SortableCriteria implements IPageable {
  private boolean enablePage  = true;
  private int     currentPage = 1;
  private int     pageSize    = 20;


  @Override
  public boolean getEnablePage() {
    return enablePage;
  }

  @Override
  public int getCurrentPage() {
    return currentPage;
  }

  @Override
  public int getPageSize() {
    return pageSize;
  }

  public void setEnablePage(boolean enablePage) {
    this.enablePage = enablePage;
  }

  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
}
