package work.myfavs.framework.orm.meta.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页对象
 *
 * @param <TModel> 简单分页对象泛型
 */
public class Page<TModel>
    implements Serializable {

  //region Attributes
  private List<TModel> data         = new ArrayList<>();
  private int          currentPage  = 1;
  private int          pageSize     = 20;
  private long         totalPages   = 1L;
  private long         totalRecords = 0L;
  //endregion

  //region Getter && Setter
  public List<TModel> getData() {

    return data;
  }

  public void setData(List<TModel> data) {

    this.data = data;
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

  public long getTotalPages() {

    return totalPages;
  }

  public void setTotalPages(long totalPages) {

    this.totalPages = totalPages;
  }

  public long getTotalRecords() {

    return totalRecords;
  }

  public void setTotalRecords(long totalRecords) {

    this.totalRecords = totalRecords;
  }
  //endregion

  //region Constructor
  private Page() {

  }
  //endregion

  /**
   * 创建分页对象
   *
   * @param <TModel> 分页对象数据类型泛型
   *
   * @return 分页对象
   */
  public static <TModel> Page<TModel> createInstance() {

    return new Page<>();
  }

  /**
   * 创建分页对象
   *
   * @param data         分页数据
   * @param currentPage  当前页码
   * @param pageSize     每页记录数
   * @param totalPages   总页数
   * @param totalRecords 总记录数
   * @param <TModel>     分页对象数据类型泛型
   *
   * @return 分页对象
   */
  public static <TModel> Page<TModel> createInstance(List<TModel> data,
                                                     int currentPage,
                                                     int pageSize,
                                                     long totalPages,
                                                     long totalRecords) {

    Page<TModel> instance = new Page<>();
    instance.setData(data);
    instance.setCurrentPage(currentPage);
    instance.setPageSize(pageSize);
    instance.setTotalPages(totalPages);
    instance.setTotalRecords(totalRecords);
    return instance;
  }

  /**
   * 转换分页对象数据
   *
   * @param data     分页数据
   * @param <TOther> 分页数据类型泛型
   *
   * @return 新分页数据
   */
  public <TOther> Page<TOther> convert(List<TOther> data) {

    return createInstance(data, this.currentPage, this.pageSize, this.totalPages, this.totalRecords);
  }

}
