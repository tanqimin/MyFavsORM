package work.myfavs.framework.example.repository.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.example.domain.entity.TestSnowFake;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.PageLite;

@Repository
public class SnowFakeQuery
    extends BaseQuery {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public SnowFakeQuery(@Qualifier("primaryDBTemplate") DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  public PageLite<TestSnowFake> findPageLite(long currentPage, long pageSize) {

    Sql querySql = new Sql("SELECT * FROM tb_snowfake").orderBy("id DESC", "created ASC");
    return this.findPageLite(TestSnowFake.class, querySql, true, currentPage, pageSize);
  }

  public PageLite<Record> findRecordPageLite(long currentPage, long pageSize) {

    Sql querySql = new Sql("SELECT id, created, name, price, type FROM tb_snowfake").orderBy("id DESC", "created ASC");
    return this.findPageLite(Record.class, querySql, true, currentPage, pageSize);
  }

}
