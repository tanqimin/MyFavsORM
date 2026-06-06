package work.myfavs.framework.sb2.demo.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.sb2.demo.business.TenantService;
import work.myfavs.framework.sb2.demo.domain.entity.Tenant;

@RestController
@RequestMapping("/tenant")
/**
 * 租户管理控制器，提供租户增删改查接口.
 */
public class TenantController {
  private final TenantService tenantService;

  public TenantController(TenantService userService) {
    this.tenantService = userService;
  }

  /**
   * 保存租户.
   *
   * @param entity 租户请求实体
   * @return 租户 ID
   */
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public ResponseEntity<Long> saveTenant(RequestEntity<Tenant> entity) {
    Tenant tenant = tenantService.saveTenant(entity.getBody());
    return ResponseEntity.ok().body(tenant.getId());
  }

  /**
   * 分页查询租户.
   *
   * @return 租户分页数据
   */
  @RequestMapping(value = "/find-by-page")
  public ResponseEntity<Page<Tenant>> findByPage(){
    return ResponseEntity.ok().body(tenantService.findByPage());
  }
}
