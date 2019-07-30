package work.myfavs.framework.example.repository.query;

import lombok.extern.slf4j.Slf4j;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Query;
import work.myfavs.framework.orm.repository.monitor.SqlExecutedContext;
import work.myfavs.framework.orm.repository.monitor.SqlExecutingContext;

@Slf4j
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

  @Override
  protected void afterQuery(SqlExecutedContext context) {

  }

  @Override
  protected void beforeQuery(SqlExecutingContext context) {

  }

}
