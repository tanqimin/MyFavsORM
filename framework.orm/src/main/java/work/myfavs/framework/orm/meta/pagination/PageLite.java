package work.myfavs.framework.orm.meta.pagination;

import java.util.List;
import java.util.function.Function;

/**
 * 简单分页对象
 *
 * @param <TModel> 简单分页对象泛型
 */
public class PageLite<TModel>
    extends PageBase<TModel> {

  //region Attributes
  private boolean hasNext = false;
  //endregion

  //region Getter && Setter

  public boolean isHasNext() {

    return hasNext;
  }

  public void setHasNext(boolean hasNext) {

    this.hasNext = hasNext;
  }
  //endregion

  //region Constructor
  private PageLite() {

  }
  //endregion

  /**
   * 创建简单分页对象实例
   *
   * @param <TModel> 简单分页对象泛型
   *
   * @return 简单分页对象
   */
  public static <TModel> PageLite<TModel> createInstance() {

    return new PageLite<>();
  }

  /**
   * 创建简单分页对象实例
   *
   * @param data        分页数据
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TModel>    简单分页对象泛型
   *
   * @return 简单分页对象
   */
  public static <TModel> PageLite<TModel> createInstance(List<TModel> data,
                                                         long currentPage,
                                                         long pageSize) {

    PageLite<TModel> instance = new PageLite<>();
    instance.setData(data);
    instance.setCurrentPage(currentPage);
    instance.setPageSize(pageSize);
    if (data != null) {
      instance.setHasNext(data.size() == pageSize);
    }
    return instance;
  }

  /**
   * 转换简单分页对象数据
   *
   * @param data     分页数据
   * @param <TOther> 分页数据类型泛型
   *
   * @return 新分页数据
   */
  public <TOther> PageLite<TOther> convert(List<TOther> data) {

    return createInstance(data, this.getCurrentPage(), this.getPageSize());
  }

  /**
   * 转换简单分页对象数据
   *
   * @param fun      转换Function
   * @param <TOther> 分页数据类型泛型
   *
   * @return 新分页数据
   */
  public <TOther> PageLite<TOther> convert(Function<TModel, TOther> fun) {

    return convert(convertData(fun));
  }


}
