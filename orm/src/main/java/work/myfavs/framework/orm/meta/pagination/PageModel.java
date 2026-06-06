package work.myfavs.framework.orm.meta.pagination;

import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.DBTemplate;

import java.util.HashMap;
import java.util.Objects;

/**
 * 页面模型，如果需要返回自定义的格式，请使用此类封装分页
 *
 * @param <TModel>
 */
public class PageModel<TModel> extends HashMap<String, Object> {

  private String dataField         = "data";
  private String currentPageField  = "currentPage";
  private String totalPagesField   = "totalPages";
  private String totalRecordsField = "totalRecords";
  private String pageSizeField     = "pageSize";
  private String hasNetField       = "hasNext";

  private DBTemplate dbTemplate;

  /**
   * 构造页面模型
   */
  public PageModel() {
  }

  /**
   * 构造页面模型，使用 {@link DBTemplate} 中的配置初始化字段名称
   *
   * @param dbTemplate {@link DBTemplate} 实例
   */
  public PageModel(DBTemplate dbTemplate) {
    Objects.requireNonNull(dbTemplate, "dbTemplate is marked non-null but is null");
    DBConfig dbConfig = dbTemplate.getDbConfig();
    dataField = dbConfig.getPageDataField();
    currentPageField = dbConfig.getPageCurrentField();
    totalPagesField = dbConfig.getPageTotalPageField();
    totalRecordsField = dbConfig.getPageTotalRecordField();
    pageSizeField = dbConfig.getPageSizeField();
    hasNetField = dbConfig.getPageHasNextField();
  }

  /**
   * 将 {@link Page} 分页对象转换为页面模型
   *
   * @param page {@link Page} 分页对象
   * @return 当前页面模型实例
   */
  public PageModel<TModel> convert(Page<TModel> page) {
    this.put(dataField, page.getData());
    this.put(currentPageField, page.getCurrentPage());
    this.put(totalPagesField, page.getTotalPages());
    this.put(totalRecordsField, page.getTotalRecords());
    this.put(pageSizeField, page.getPageSize());
    return this;
  }

  /**
   * 将 {@link PageLite} 简单分页对象转换为页面模型
   *
   * @param pageLite {@link PageLite} 简单分页对象
   * @return 当前页面模型实例
   */
  public PageModel<TModel> convert(PageLite<TModel> pageLite) {
    this.put(dataField, pageLite.getData());
    this.put(currentPageField, pageLite.getCurrentPage());
    this.put(pageSizeField, pageLite.getPageSize());
    this.put(hasNetField, pageLite.isHasNext());
    return this;
  }
}
