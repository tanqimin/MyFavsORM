package work.myfavs.framework.example.repository.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.example.repository.BaseQuery;
import work.myfavs.framework.orm.DBTemplate;

@Repository
public class UuidQuery
    extends BaseQuery {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public UuidQuery(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

}