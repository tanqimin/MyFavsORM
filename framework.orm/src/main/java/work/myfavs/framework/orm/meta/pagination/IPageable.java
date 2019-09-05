package work.myfavs.framework.orm.meta.pagination;

public interface IPageable {


  boolean getEnablePage();

  int getCurrentPage();

  int getPageSize();

}
