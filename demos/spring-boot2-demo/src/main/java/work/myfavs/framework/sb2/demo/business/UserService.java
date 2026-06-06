package work.myfavs.framework.sb2.demo.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.orm.business.BaseService;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.sb2.demo.domain.entity.User;
import work.myfavs.framework.sb2.demo.repository.repo.UserRepository;

import java.util.Date;

@Service
/**
 * 用户业务服务，提供用户保存、更新和分页查询.
 */
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
   * @param entity 用户实体（包含更新字段）
   * @return 更新记录数
   */
  @Transactional(rollbackFor = Exception.class)
  public Long updateUser(Long id, User entity) {
    User user = userRepository.getById(id);
    if(null == user) return 0L;
    user.setUsername(entity.getUsername());
    user.setPassword(entity.getPassword());
    user.setEmail(entity.getEmail());
    user.setModified(new Date());

    return (long) userRepository.update(user);
  }
}
