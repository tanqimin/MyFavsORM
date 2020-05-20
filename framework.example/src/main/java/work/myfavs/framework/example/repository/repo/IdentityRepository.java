package work.myfavs.framework.example.repository.repo;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.example.domain.entity.Identity;
import work.myfavs.framework.example.domain.entity.Identity.META;
import work.myfavs.framework.example.repository.BaseRepository;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * Identity Repository PS: 此文件通过代码生成器生成
 */
@Repository
public class IdentityRepository
    extends BaseRepository<Identity> {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public IdentityRepository(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

  public Map<String, Identity> findMap() {
    Sql sql = new Sql("SELECT * FROM " + META.TABLE);
    return super.findMap(Identity.class, "name", sql);
  }
}