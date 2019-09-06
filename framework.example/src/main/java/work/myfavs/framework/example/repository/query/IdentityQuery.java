package work.myfavs.framework.example.repository.query;

import static work.myfavs.framework.example.domain.entity.Identity.META;

import work.myfavs.framework.example.domain.entity.Identity;
import work.myfavs.framework.example.repository.BaseQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * Identity Query
 * PS: 此文件通过代码生成器生成
 */
@Repository
public class IdentityQuery
    extends BaseQuery {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public IdentityQuery(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  /**
   * 根据主键获取 Identity
   *
   * @param id 主键
   *
   * @return Identity
   */
  public Identity getById(Object id) {

    Sql sql = Sql.Select("*").from(META.TABLE).where(Cond.eq(META.COLUMNS.id, id));
    return super.get(Identity.class, sql);
  }
}