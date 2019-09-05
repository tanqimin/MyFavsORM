package work.myfavs.framework.example.repository.query;

import work.myfavs.framework.example.repository.BaseQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.orm.DBTemplate;

@Repository
public class SnowfakeQuery extends BaseQuery {
  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public SnowfakeQuery(DBTemplate dbTemplate) {

    super(dbTemplate);
  }
}