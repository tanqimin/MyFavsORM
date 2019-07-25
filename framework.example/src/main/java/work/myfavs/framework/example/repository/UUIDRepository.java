package work.myfavs.framework.example.repository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import work.myfavs.framework.example.domain.entity.TestUUID;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;

@org.springframework.stereotype.Repository
public class UUIDRepository
    extends BaseRepository<TestUUID> {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public UUIDRepository(@Qualifier("primaryDBTemplate") DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  public List<TestUUID> findAllDrink() {

    return super.findByField("type", TypeEnum.DRINK);
  }

  public List<String> findFoodsIds() {

    Sql sql = Sql.Select("id").from("tb_uuid").where(Cond.eq("type", TypeEnum.FOOD));
    return super.find(String.class, sql);
  }

}
