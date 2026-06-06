package work.myfavs.framework.orm.meta.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 分页基类
 *
 * @author tanqimin
 */
public abstract class PageBase<TModel> implements Serializable {

  private List<TModel> data = new ArrayList<>();
  private long         currentPage;
  private long         pageSize;

  /**
   * 获取分页数据列表
   *
   * @return 分页数据列表
   */
  public List<TModel> getData() {
    return data;
  }

  /**
   * 设置分页数据列表
   *
   * @param data 分页数据列表
   */
  public void setData(List<TModel> data) {
    this.data = data;
  }

  /**
   * 获取当前页码
   *
   * @return 当前页码
   */
  public long getCurrentPage() {
    return currentPage;
  }

  /**
   * 设置当前页码
   *
   * @param currentPage 当前页码
   */
  public void setCurrentPage(long currentPage) {
    this.currentPage = currentPage;
  }

  /**
   * 获取每页记录数
   *
   * @return 每页记录数
   */
  public long getPageSize() {
    return pageSize;
  }

  /**
   * 设置每页记录数
   *
   * @param pageSize 每页记录数
   */
  public void setPageSize(long pageSize) {
    this.pageSize = pageSize;
  }

  protected <TOther> List<TOther> convertData(Function<TModel, TOther> fun) {

    List<TOther> list = new ArrayList<>();
    for (TModel item : this.getData()) {
      TOther apply = fun.apply(item);
      list.add(apply);
    }
    return list;
  }
}
