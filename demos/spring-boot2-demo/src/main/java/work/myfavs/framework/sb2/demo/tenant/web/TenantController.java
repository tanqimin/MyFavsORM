package work.myfavs.framework.sb2.demo.tenant.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.sb2.demo.tenant.Tenant;
import work.myfavs.framework.sb2.demo.tenant.service.TenantService;

/**
 * 租户管理控制器，提供租户增删改查接口.
 */
@RestController
@RequestMapping("/tenant")
public class TenantController {

  private final TenantService tenantService;

  public TenantController(TenantService tenantService) {
    this.tenantService = tenantService;
  }

  /**
   * 保存租户.
   *
   * @param entity 租户请求实体
   * @return 租户 ID
   */
  @PostMapping("/save")
  public ResponseEntity<Long> saveTenant(@RequestBody Tenant entity) {
    Tenant tenant = tenantService.saveTenant(entity);
    return ResponseEntity.ok(tenant.getId());
  }

  /**
   * 分页查询租户.
   *
   * @return 租户分页数据
   */
  @GetMapping("/find-by-page")
  public ResponseEntity<Page<Tenant>> findByPage() {
    return ResponseEntity.ok(tenantService.findByPage());
  }
}
