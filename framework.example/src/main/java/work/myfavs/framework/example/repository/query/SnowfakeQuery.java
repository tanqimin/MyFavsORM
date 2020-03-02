package work.myfavs.framework.example.repository.query;

import static work.myfavs.framework.example.domain.entity.Snowfake.META;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.Snowfake;
import work.myfavs.framework.example.repository.BaseQuery;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;

/**
 * Snowfake Query
 * PS: 此文件通过代码生成器生成
 */
@Repository
public class SnowfakeQuery
    extends BaseQuery {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public SnowfakeQuery(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  /**
   * 根据主键获取 Snowfake
   *
   * @param id 主键
   *
   * @return Snowfake
   */
  public Snowfake getById(Object id) {

    Sql sql = Sql.Select("*")
                 .from(META.TABLE)
                 .where(Cond.eq(META.COLUMNS.id, id));
    return super.get(Snowfake.class, sql);
  }

  @Transactional(readOnly = true)
  public Page<Snowfake> findPage() {

    return super.findPage(Snowfake.class, Sql.Select("*")
                                             .from(META.TABLE), true, 1, 1);
  }

  public Snowfake getFirst() {

    return super.get(Snowfake.class, "select * from " + META.TABLE, null);
  }

}