package work.myfavs.framework.example.business;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.User;
import work.myfavs.framework.example.repository.repo.UserRepository;
import work.myfavs.framework.orm.business.BaseService;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;

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
    return userRepository.findPage(User.class, new Sql("SELECT * FROM USER"), true, 1, 10);
  }
}
