package work.myfavs.framework.example.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Query;

public class BaseQuery
    extends Query {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  public BaseQuery(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

}