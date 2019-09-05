package work.myfavs.framework.example.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Repository;
import work.myfavs.framework.orm.monitor.SqlAnalysis;
import work.myfavs.framework.orm.monitor.SqlExecutedEvent;
import work.myfavs.framework.orm.monitor.SqlExecutingEvent;

public class BaseRepository<TModel> extends Repository<TModel> {
  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  public BaseRepository(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  @Override
  protected void afterQuery(SqlExecutedEvent context) {

  }

  @Override
  protected void beforeQuery(SqlExecutingEvent context) {

  }

  @Override
  protected void afterExecute(SqlExecutedEvent context) {

  }

  @Override
  protected void beforeExecute(SqlExecutingEvent context) {

  }

}