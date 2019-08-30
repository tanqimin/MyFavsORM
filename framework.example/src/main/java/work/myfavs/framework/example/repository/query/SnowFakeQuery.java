package work.myfavs.framework.example.repository.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.example.domain.entity.Snowfake;
import work.myfavs.framework.example.repository.BaseQuery;
import work.myfavs.framework.orm.DBTemplate;

@Repository
public class SnowFakeQuery
    extends BaseQuery {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public SnowFakeQuery(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  public Snowfake get() {

    return super.get(Snowfake.class, "SELECT * FROM tb_snowfake");
  }

}