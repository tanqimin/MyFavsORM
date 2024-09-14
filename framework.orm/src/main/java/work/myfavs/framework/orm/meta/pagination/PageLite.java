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
    PageLite<TOther> pageLite = new PageLite<>();
    pageLite.setData(data);
    pageLite.setCurrentPage(this.getCurrentPage());
    pageLite.setPageSize(this.getPageSize());
    return pageLite;
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
}
