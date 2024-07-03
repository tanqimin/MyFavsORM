package work.myfavs.framework.orm.meta.pagination;

import work.myfavs.framework.orm.DBTemplate;

import java.util.List;
import java.util.function.Function;

/**
 * 简单分页对象
 *
 * @param <TModel> 简单分页对象泛型
 */
public class PageLite<TModel> extends PageBase<TModel> {

  protected final String  pageHasNextField;
  // region Attributes
  private final   boolean hasNext = false;
  // endregion

  // region Getter && Setter

  public boolean isHasNext() {
    return (boolean) this.get(pageHasNextField);
  }

  public void setHasNext(boolean hasNext) {
    this.put(pageHasNextField, hasNext);
  }
  // endregion

  // region Constructor

  public PageLite(DBTemplate dbTemplate) {
    super(dbTemplate);
    pageHasNextField = dbTemplate.getDbConfig().getPageHasNextField();
    this.setHasNext(false);
  }

  // endregion


  /**
   * 转换简单分页对象数据
   *
   * @param data     分页数据
   * @param <TOther> 分页数据类型泛型
   * @return 新分页数据
   */
  public <TOther> PageLite<TOther> convert(List<TOther> data) {

    return super.dbTemplate.createPageLite(data, this.getCurrentPage(), this.getPageSize());
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
