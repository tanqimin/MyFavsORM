package work.myfavs.framework.example.repository.query;

import static work.myfavs.framework.example.domain.entity.Uuid.META;

import work.myfavs.framework.example.domain.entity.Uuid;
import work.myfavs.framework.example.repository.BaseQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * Uuid Query
 * PS: 此文件通过代码生成器生成
 */
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

  /**
   * 根据主键获取 Uuid
   *
   * @param id 主键
   *
   * @return Uuid
   */
  public Uuid getById(Object id) {

    Sql sql = Sql.Select("*").from(META.TABLE).where(Cond.eq(META.COLUMNS.id, id));
    return super.get(Uuid.class, sql);
  }
}