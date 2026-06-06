package work.myfavs.framework.example.repository.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.example.domain.entity.Tenant;
import work.myfavs.framework.example.repository.BaseRepository;
import work.myfavs.framework.orm.DBTemplate;

@Repository
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
