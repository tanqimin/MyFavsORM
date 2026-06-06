package work.myfavs.framework.orm.meta.pagination;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;

/**
 * 简单分页对象
 *
 * @param <TModel> 简单分页对象泛型
 */
@Getter
@Setter
public class PageLite<TModel> extends PageBase<TModel> {

  private boolean hasNext;

  /**
   * 转换简单分页对象数据
   *
   * @param data     分页数据
   * @param <TOther> 分页数据类型泛型
   * @return 新分页数据
   */
  public <TOther> PageLite<TOther> convert(List<TOther> data) {

    return create(data, this.getCurrentPage(), this.getPageSize());
  }

  /**
   * 转换简单分页对象数据
   *
   * @param fun      转换Function
   * @param <TOther> 分页数据类型泛型
   * @return 新分页数据
   */
  public <TOther> PageLite<TOther> convert(Function<TModel, TOther> fun) {

    return convert(convertData(fun));
  }

  /**
   * 创建 {@link PageLite} 对象
   *
   * @param data        分页数据
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TModel>    简单分页对象泛型
   * @return {@link PageLite} 对象
   */
  public static <TModel> PageLite<TModel> create(
      List<TModel> data, long currentPage, long pageSize) {

    PageLite<TModel> instance = new PageLite<>();
    instance.setData(data);
    instance.setCurrentPage(currentPage);
    instance.setPageSize(pageSize);
    if (null != data) {
      instance.setHasNext(data.size() == pageSize);
    }
    return instance;
  }
}
