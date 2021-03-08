package work.myfavs.framework.orm.meta.pagination;

import java.util.List;
import java.util.function.Function;
import work.myfavs.framework.orm.DBTemplate;

/**
 * 简单分页对象
 *
 * @param <TModel> 简单分页对象泛型
 */
public class PageLite<TModel>
    extends PageBase<TModel> {

  protected final String  pageHasNextField;
  //region Attributes
  private         boolean hasNext = false;
  //endregion

  //region Getter && Setter

  public boolean isHasNext() {
    return (boolean) this.get(pageHasNextField);
  }

  public void setHasNext(boolean hasNext) {
    this.put(pageHasNextField, hasNext);
  }
  //endregion

  //region Constructor

  public PageLite(DBTemplate dbTemplate) {
    super(dbTemplate);
    pageHasNextField = dbTemplate.getDbConfig().getPageHasNextField();
    this.setHasNext(false);
  }

  //endregion

  /**
   * 创建简单分页对象实例
   *
   * @param dbTemplate DBTemplate
   * @param <TModel>   简单分页对象泛型
   * @return 简单分页对象
   */
  public static <TModel> PageLite<TModel> createInstance(DBTemplate dbTemplate) {

    return new PageLite<>(dbTemplate);
  }

  /**
   * 创建简单分页对象实例
   *
   * @param dbTemplate  DBTemplate
   * @param data        分页数据
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TModel>    简单分页对象泛型
   * @return 简单分页对象
   */
  public static <TModel> PageLite<TModel> createInstance(DBTemplate dbTemplate,
      List<TModel> data,
      long currentPage,
      long pageSize) {

    PageLite<TModel> instance = createInstance(dbTemplate);
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
   * @return 新分页数据
   */
  public <TOther> PageLite<TOther> convert(List<TOther> data) {

    return createInstance(super.dbTemplate, data, this.getCurrentPage(), this.getPageSize());
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
