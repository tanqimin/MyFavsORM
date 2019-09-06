package work.myfavs.framework.example.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Query;
import work.myfavs.framework.orm.monitor.SqlAnalysis;
import work.myfavs.framework.orm.monitor.SqlExecutedEvent;
import work.myfavs.framework.orm.monitor.SqlExecutingEvent;

/**
 * Query 基类
 * PS: 此文件通过代码生成器生成
 */
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