package work.myfavs.framework.sb2.demo.business;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.orm.business.BaseService;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.sb2.demo.domain.entity.Tenant;
import work.myfavs.framework.sb2.demo.repository.repo.TenantRepository;
import work.myfavs.framework.sb2.demo.util.tenant.DynamicDataSource;

@Service
/**
 * 租户业务服务，提供租户查询、保存和分页查询.
 */
public class TenantService extends BaseService {
  private final TenantRepository tenantRepository;
  private final DynamicDataSource dynamicDataSource;

  public TenantService(TenantRepository tenantRepository, @Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) {
    this.tenantRepository = tenantRepository;
    this.dynamicDataSource = dynamicDataSource;
  }

  /**
   * 根据租户标识获取租户.
   *
   * @param tenant 租户标识
   * @return 租户实体
   */
  public Tenant getByTenant(String tenant) {
    return tenantRepository.getByField("tenant", tenant);
  }

  /**
   * 保存租户并添加数据源（包含事务管理）.
   *
   * @param tenant 租户实体
   * @return 保存后的租户实体
   */
  @Transactional(rollbackFor = Exception.class)
  public Tenant saveTenant(Tenant tenant) {
    tenantRepository.create(tenant);
    dynamicDataSource.addDataSource(tenant);
    return tenant;
  }

  /**
   * 分页查询租户.
   *
   * @return 租户分页数据
   */
  public Page<Tenant> findByPage() {
    return tenantRepository.findPage(Tenant.class, new Sql("WITH QUERY AS (SELECT * FROM tb_tenant) SELECT * FROM QUERY"), true, 1, 10);
  }
}
