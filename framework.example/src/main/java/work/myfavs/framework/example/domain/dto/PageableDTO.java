package work.myfavs.framework.example.domain.dto;

import work.myfavs.framework.orm.meta.pagination.IPageable;

public class PageableDTO implements IPageable {

  private boolean enablePage = false;
  private int currentPage;
  private int pageSize;

  public boolean getEnablePage() {

    return enablePage;
  }

  public void setEnablePage(boolean enablePage) {

    this.enablePage = enablePage;
  }

  public int getCurrentPage() {

    return currentPage;
  }

  public void setCurrentPage(int currentPage) {

    this.currentPage = currentPage;
  }

  public int getPageSize() {

    return pageSize;
  }

  public void setPageSize(int pageSize) {

    this.pageSize = pageSize;
  }
}
