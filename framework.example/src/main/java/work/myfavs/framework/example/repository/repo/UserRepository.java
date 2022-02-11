package work.myfavs.framework.example.repository.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.example.domain.entity.User;
import work.myfavs.framework.example.repository.BaseRepository;
import work.myfavs.framework.orm.DBTemplate;

@Repository
public class UserRepository extends BaseRepository<User> {
  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public UserRepository(DBTemplate dbTemplate) {
    super(dbTemplate);
  }
}
