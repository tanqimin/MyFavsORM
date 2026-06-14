package work.myfavs.framework.sb2.demo.user.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.sb2.demo.tenant.TestNvarchar;
import work.myfavs.framework.sb2.demo.user.User;
import work.myfavs.framework.sb2.demo.user.dto.UserUpdateRequest;
import work.myfavs.framework.sb2.demo.user.service.UserService;

/**
 * 用户管理控制器，提供用户增改查及 NVarchar 测试接口.
 */
@RestController
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * 保存用户.
   *
   * @param user 用户实体
   * @return 用户 ID
   */
  @PostMapping("/save")
  public ResponseEntity<Long> saveUser(@RequestBody User user) {
    User saved = userService.saveUser(user);
    return ResponseEntity.ok(saved.getId());
  }

  /**
   * 更新用户.
   *
   * @param id         用户 ID
   * @param updateReq  更新请求（含 username、password、email）
   * @return 更新记录数
   */
  @PostMapping("/update/{id}")
  public ResponseEntity<Long> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest updateReq) {
    return ResponseEntity.ok(userService.updateUser(id, updateReq));
  }

  /**
   * 分页查询用户.
   *
   * @return 用户分页数据
   */
  @GetMapping("/find-by-page")
  public ResponseEntity<Page<User>> findByPage() {
    return ResponseEntity.ok(userService.findByPage());
  }

  /**
   * 测试 NVarchar 序列化与反序列化.
   *
   * @param entity 测试请求实体
   * @return 原样返回的测试实体
   */
  @PostMapping("/test-nvarchar")
  public ResponseEntity<TestNvarchar> testNvarchar(@RequestBody TestNvarchar entity) {
    return ResponseEntity.ok(entity);
  }
}
