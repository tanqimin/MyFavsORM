package work.myfavs.framework.orm.meta.pagination;

import work.myfavs.framework.orm.DBTemplate;

import java.util.List;
import java.util.function.Function;

/**
 * 分页对象
 *
 * @param <TModel> 简单分页对象泛型
 * @author tanqimin
 */
public class Page<TModel> extends PageBase<TModel> {

  protected final String pageTotalPageField;
  protected final String pageTotalRecordField;

  // region Getter && Setter
  public long getTotalPages() {
    return (long) this.get(pageTotalPageField);
  }

  public void setTotalPages(long totalPages) {
    this.put(pageTotalPageField, totalPages);
  }

  public long getTotalRecords() {
    return (long) this.get(pageTotalRecordField);
  }

  public void setTotalRecords(long totalRecords) {
    this.put(pageTotalRecordField, totalRecords);
  }
  // endregion

  // region Constructor

  public Page(DBTemplate dbTemplate) {
    super(dbTemplate);
    this.pageTotalPageField = dbTemplate.getDbConfig().getPageTotalPageField();
    this.pageTotalRecordField = dbTemplate.getDbConfig().getPageTotalRecordField();

    this.setTotalPages(1L);
    this.setTotalRecords(0L);
  }

  // endregion

  /**
   * 转换分页对象数据
   *
   * @param data     分页数据
   * @param <TOther> 分页数据类型泛型
   * @return 新分页数据
   */
  public <TOther> Page<TOther> convert(List<TOther> data) {
    return super.dbTemplate.createPage(
        data,
        this.getCurrentPage(),
        this.getPageSize(),
        this.getTotalPages(),
        this.getTotalRecords());
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
}
