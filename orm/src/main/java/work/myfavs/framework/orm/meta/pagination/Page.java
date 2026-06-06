package work.myfavs.framework.orm.meta.pagination;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

/**
 * 分页对象
 *
 * @param <TModel> 简单分页对象泛型
 * @author tanqimin
 */
@Getter
@Setter
public class Page<TModel> extends PageBase<TModel> {

  private long totalPages = 1L;
  private long totalRecords;

  /**
   * 转换分页对象数据
   *
   * @param data     分页数据
   * @param <TOther> 分页数据类型泛型
   * @return 新分页数据
   */
  public <TOther> Page<TOther> convert(List<TOther> data) {
    Page<TOther> page = new Page<>();
    page.setData(data);
    page.setCurrentPage(this.getCurrentPage());
    page.setTotalPages(this.getTotalPages());
    page.setTotalRecords(this.getTotalRecords());
    page.setPageSize(this.getPageSize());
    return page;
  }

  /**
   * 转换分页对象数据
   *
   * @param fun      转换Function
   * @param <TOther> 分页数据类型泛型
   * @return 新分页数据
   */
  public <TOther> Page<TOther> convert(Function<TModel, TOther> fun) {
    return convert(convertData(fun));
  }

  /**
   * 创建 {@link Page} 对象
   *
   * @param data         分页数据
   * @param currentPage  当前页码
   * @param pageSize     每页记录数
   * @param totalPages   总页数
   * @param totalRecords 总记录数
   * @param <TView>      分页对象数据类型泛型
   * @return {@link Page} 对象
   */
  public static <TView> Page<TView> create(List<TView> data, long currentPage, long pageSize, long totalPages, long totalRecords) {
    Page<TView> page = new Page<>();
    page.setData(data);
    page.setCurrentPage(currentPage);
    page.setPageSize(pageSize);
    page.setTotalPages(totalPages);
    page.setTotalRecords(totalRecords);
    return page;
  }
}
