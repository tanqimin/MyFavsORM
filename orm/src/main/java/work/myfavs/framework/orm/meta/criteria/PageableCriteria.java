package work.myfavs.framework.orm.meta.criteria;

import work.myfavs.framework.orm.meta.pagination.IPageable;

/**
 * 分页查询条件基类，继承 {@link SortableCriteria} 并实现 {@link IPageable} 接口。
 * <p>同时支持排序和分页，可直接作为查询参数传递。
 * 默认启用分页，当前页码为 1，每页记录数为 20。</p>
 *
 * @see SortableCriteria
 * @see IPageable
 */
public abstract class PageableCriteria extends SortableCriteria implements IPageable {
  private boolean enablePage  = true;
  private int     currentPage = 1;
  private int     pageSize    = 20;


  /**
   * 是否启用分页
   *
   * @return 启用分页返回 {@code true}
   */
  @Override
  public boolean getEnablePage() {
    return enablePage;
  }

  /**
   * 获取当前页码
   *
   * @return 当前页码
   */
  @Override
  public int getCurrentPage() {
    return currentPage;
  }

  /**
   * 获取每页记录数
   *
   * @return 每页记录数
   */
  @Override
  public int getPageSize() {
    return pageSize;
  }

  /**
   * 设置是否启用分页
   *
   * @param enablePage 是否启用分页
   */
  public void setEnablePage(boolean enablePage) {
    this.enablePage = enablePage;
  }

  /**
   * 设置当前页码
   *
   * @param currentPage 当前页码
   */
  public void setCurrentPage(int currentPage) {
    this.currentPage = currentPage;
  }

  /**
   * 设置每页记录数
   *
   * @param pageSize 每页记录数
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }
}
