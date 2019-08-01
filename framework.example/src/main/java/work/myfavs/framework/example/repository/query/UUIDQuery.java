package work.myfavs.framework.example.repository.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.TestUUID;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;

@Repository
public class UUIDQuery
    extends BaseQuery {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public UUIDQuery(@Qualifier("primaryDBTemplate") DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  @Transactional(readOnly = true)
  public Page<TestUUID> findPage(int currentPage, int pageSize) {

    Sql querySql = new Sql("SELECT * FROM tb_uuid").where(Cond.eq("type", TypeEnum.FOOD)).orderBy("id DESC", "created ASC");
    return this.findPage(TestUUID.class, querySql, true, currentPage, pageSize);
  }

  @Transactional(readOnly = true)
  public PageLite<TestUUID> findPageLite(int currentPage, int pageSize) {

    Sql querySql = new Sql("SELECT * FROM tb_uuid").orderBy("id DESC", "created ASC");
    return this.findPageLite(TestUUID.class, querySql, true, currentPage, pageSize);
  }

}
