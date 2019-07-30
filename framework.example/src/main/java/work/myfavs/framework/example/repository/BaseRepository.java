package work.myfavs.framework.example.repository;

import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Repository;
import work.myfavs.framework.orm.repository.monitor.SqlExecutedContext;
import work.myfavs.framework.orm.repository.monitor.SqlExecutingContext;

@Slf4j
public class BaseRepository<TModel>
    extends Repository<TModel> {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  public BaseRepository(DBTemplate dbTemplate) {

    super(dbTemplate);
  }


  @Override
  protected void afterQuery(SqlExecutedContext context) {

  }

  @Override
  protected void beforeQuery(SqlExecutingContext context) {

  }

  @Override
  protected void afterExecute(SqlExecutedContext context) {

  }

  @Override
  protected void beforeExecute(SqlExecutingContext context) {

  }

}
