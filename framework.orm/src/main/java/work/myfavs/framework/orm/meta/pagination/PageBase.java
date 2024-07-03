package work.myfavs.framework.orm.meta.pagination;

import work.myfavs.framework.orm.DBTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * 分页基类
 *
 * @author tanqimin
 */
public class PageBase<TModel> extends HashMap<String, Object> {

  protected final DBTemplate dbTemplate;
  protected final String     pageDataField;
  protected final String     pageCurrentField;
  protected final String     pageSizeField;

  public PageBase(DBTemplate dbTemplate) {
    this.dbTemplate = dbTemplate;
    this.pageDataField = dbTemplate.getDbConfig().getPageDataField();
    this.pageCurrentField = dbTemplate.getDbConfig().getPageCurrentField();
    this.pageSizeField = dbTemplate.getDbConfig().getPageSizeField();
  }

  @SuppressWarnings("unchecked")
  public List<TModel> getData() {
    return (List<TModel>) this.get(pageDataField);
  }

  public void setData(List<TModel> data) {
    this.put(pageDataField, data);
  }

  public long getCurrentPage() {
    return (long) this.get(pageCurrentField);
  }

  public void setCurrentPage(long currentPage) {
    this.put(pageCurrentField, currentPage);
  }

  public long getPageSize() {

    return (long) this.get(pageSizeField);
  }

  public void setPageSize(long pageSize) {
    this.put(pageSizeField, pageSize);
  }

  protected <TOther> List<TOther> convertData(Function<TModel, TOther> fun) {

    List<TOther> list = new ArrayList<>();
    for (TModel item : this.getData()) {
      TOther apply = fun.apply(item);
      list.add(apply);
    }
    return list;
  }
}
