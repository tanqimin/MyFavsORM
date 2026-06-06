package work.myfavs.framework.orm.meta.pagination;

/**
 * 分页接口，定义分页查询所需的基本参数
 */
public interface IPageable {

  /**
   * 是否启用分页
   *
   * @return 启用分页返回 {@code true}
   */
  boolean getEnablePage();

  /**
   * 获取当前页码
   *
   * @return 当前页码
   */
  int getCurrentPage();

  /**
   * 获取每页记录数
   *
   * @return 每页记录数
   */
  int getPageSize();
}
