package work.myfavs.framework.sb2.demo.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.sb2.demo.business.UserService;
import work.myfavs.framework.sb2.demo.domain.entity.TestNvarchar;
import work.myfavs.framework.sb2.demo.domain.entity.User;

@RestController
@RequestMapping("/user")
/**
 * 用户管理控制器，提供用户增改查及 NVarchar 测试接口.
 */
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * 保存用户.
   *
   * @param entity 用户请求实体
   * @return 用户 ID
   */
  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public ResponseEntity<Long> saveUser(RequestEntity<User> entity) {
    User user = userService.saveUser(entity.getBody());
    return ResponseEntity.ok().body(user.getId());
  }

  /**
   * 更新用户.
   *
   * @param id     用户 ID
   * @param entity 用户请求实体
   * @return 更新记录数
   */
  @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
  public ResponseEntity<Long> updateUser(@PathVariable Long id, RequestEntity<User> entity) {
    return ResponseEntity.ok().body(userService.updateUser(id, entity.getBody()));
  }

  /**
   * 分页查询用户.
   *
   * @return 用户分页数据
   */
  @RequestMapping(value = "/find-by-page")
  public ResponseEntity<Page<User>> findByPage() {
    return ResponseEntity.ok().body(userService.findByPage());
  }

  /**
   * 测试 NVarchar 序列化与反序列化.
   *
   * @param entity 测试请求实体
   * @return 原样返回的测试实体
   */
  @RequestMapping(value = "/test-nvarchar", method = RequestMethod.POST)
  public ResponseEntity<TestNvarchar> testNvarchar(RequestEntity<TestNvarchar> entity) {

    return ResponseEntity.ok().body(entity.getBody());
  }
}
