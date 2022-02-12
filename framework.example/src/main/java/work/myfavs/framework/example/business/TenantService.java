package work.myfavs.framework.example.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.Tenant;
import work.myfavs.framework.example.domain.entity.User;
import work.myfavs.framework.example.repository.repo.TenantRepository;
import work.myfavs.framework.orm.business.BaseService;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;

@Service
public class TenantService extends BaseService {
  private final TenantRepository tenantRepository;

  public TenantService(TenantRepository tenantRepository) {
    this.tenantRepository = tenantRepository;
  }

  public Tenant getByTenant(String tenant) {
    return tenantRepository.getByField("tenant", tenant);
  }
}
