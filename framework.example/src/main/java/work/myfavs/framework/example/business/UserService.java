package work.myfavs.framework.example.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.User;
import work.myfavs.framework.example.repository.repo.UserRepository;
import work.myfavs.framework.orm.business.BaseService;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;

import java.util.Date;

@Service
public class UserService extends BaseService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(rollbackFor = Exception.class)
  public User saveUser(User user) {
    userRepository.create(user);
    return user;
  }

  public Page<User> findByPage() {
    Page<User> page = userRepository.findPage(User.class, new Sql("SELECT * FROM tb_user"), true, 1, 10);
    return page;
  }

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
