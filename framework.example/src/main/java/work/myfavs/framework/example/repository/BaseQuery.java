package work.myfavs.framework.example.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Query;
import work.myfavs.framework.orm.monitor.SqlAnalysis;
import work.myfavs.framework.orm.monitor.SqlExecutedEvent;
import work.myfavs.framework.orm.monitor.SqlExecutingEvent;

public class BaseQuery extends Query {
  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  public BaseQuery(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  @Override
  protected void afterQuery(SqlExecutedEvent context) {

  }

  @Override
  protected void beforeQuery(SqlExecutingEvent context) {

  }

}