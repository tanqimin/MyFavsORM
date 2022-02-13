package work.myfavs.framework.example.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.Tenant;
import work.myfavs.framework.example.domain.entity.User;
import work.myfavs.framework.example.repository.repo.TenantRepository;
import work.myfavs.framework.example.util.tenant.DynamicDataSource;
import work.myfavs.framework.orm.business.BaseService;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;

@Service
public class TenantService extends BaseService {
  private final TenantRepository tenantRepository;
  private final DynamicDataSource dynamicDataSource;

  public TenantService(TenantRepository tenantRepository, @Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) {
    this.tenantRepository = tenantRepository;
    this.dynamicDataSource = dynamicDataSource;
  }

  public Tenant getByTenant(String tenant) {
    return tenantRepository.getByField("tenant", tenant);
  }

  @Transactional(rollbackFor = Exception.class)
  public Tenant saveTenant(Tenant tenant) {
    tenantRepository.create(tenant);
    dynamicDataSource.addDataSource(tenant);
    return tenant;
  }

  public Page<Tenant> findByPage() {
    return tenantRepository.findPage(Tenant.class, new Sql("SELECT * FROM tenant"), true, 1, 10);
  }
}
