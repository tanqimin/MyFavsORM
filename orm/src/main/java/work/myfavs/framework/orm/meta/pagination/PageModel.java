package work.myfavs.framework.orm.meta.pagination;

import lombok.NonNull;
import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.DBTemplate;

import java.util.HashMap;

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

  public PageModel() {
  }

  public PageModel(@NonNull DBTemplate dbTemplate) {
    DBConfig dbConfig = dbTemplate.getDbConfig();
    dataField = dbConfig.getPageDataField();
    currentPageField = dbConfig.getPageCurrentField();
    totalPagesField = dbConfig.getPageTotalPageField();
    totalRecordsField = dbConfig.getPageTotalRecordField();
    pageSizeField = dbConfig.getPageSizeField();
    hasNetField = dbConfig.getPageHasNextField();
  }

  public PageModel<TModel> convert(Page<TModel> page) {
    this.put(dataField, page.getData());
    this.put(currentPageField, page.getCurrentPage());
    this.put(totalPagesField, page.getTotalPages());
    this.put(totalRecordsField, page.getTotalRecords());
    this.put(pageSizeField, page.getPageSize());
    return this;
  }

  public PageModel<TModel> convert(PageLite<TModel> pageLite) {
    this.put(dataField, pageLite.getData());
    this.put(currentPageField, pageLite.getCurrentPage());
    this.put(pageSizeField, pageLite.getPageSize());
    this.put(hasNetField, pageLite.isHasNext());
    return this;
  }
}
