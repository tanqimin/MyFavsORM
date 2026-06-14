package work.myfavs.framework.sb2.demo.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.orm.business.BaseService;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.util.lang.NVarchar;
import work.myfavs.framework.sb2.demo.user.User;
import work.myfavs.framework.sb2.demo.user.dto.UserUpdateRequest;
import work.myfavs.framework.sb2.demo.user.repository.UserRepository;

import java.util.Date;

/**
 * 用户业务服务，提供用户保存、更新和分页查询.
 */
@Service
public class UserService extends BaseService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * 保存用户（包含事务管理）.
   *
   * @param user 用户实体
   * @return 保存后的用户实体
   */
  @Transactional(rollbackFor = Exception.class)
  public User saveUser(User user) {
    userRepository.create(user);
    return user;
  }

  /**
   * 分页查询用户.
   *
   * @return 用户分页数据
   */
  public Page<User> findByPage() {
    return userRepository.findPage(User.class, new Sql("SELECT * FROM tb_user"), true, 1, 10);
  }

  /**
   * 更新用户信息（包含事务管理）.
   *
   * @param id     用户 ID
   * @param updateReq 更新请求 DTO（包含 username、password、email）
   * @return 更新记录数
   */
  @Transactional(rollbackFor = Exception.class)
  public Long updateUser(Long id, UserUpdateRequest updateReq) {
    User user = userRepository.getById(id);
    if (null == user) return 0L;
    user.setUsername(new NVarchar(updateReq.getUsername()));
    user.setPassword(updateReq.getPassword());
    user.setEmail(updateReq.getEmail());
    user.setModified(new Date());

    return (long) userRepository.update(user);
  }
}
