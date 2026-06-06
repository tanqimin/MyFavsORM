package work.myfavs.framework.sb2.demo.repository.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.sb2.demo.domain.entity.Tenant;
import work.myfavs.framework.sb2.demo.repository.BaseRepository;

@Repository
/**
 * 租户仓储，提供租户相关的数据库操作.
 */
public class TenantRepository extends BaseRepository<Tenant> {
  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public TenantRepository(DBTemplate dbTemplate) {
    super(dbTemplate);
  }
}
