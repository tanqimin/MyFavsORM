package work.myfavs.framework.sb2.demo.repository.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.sb2.demo.domain.entity.User;
import work.myfavs.framework.sb2.demo.repository.BaseRepository;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.clause.Sql;

@Repository
public class UserRepository extends BaseRepository<User> {
  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public UserRepository(@Qualifier("dbTemplate") DBTemplate dbTemplate) {
    super(dbTemplate);
  }

    public User getByIdForUpdate(Long id) {
      Sql sql = new Sql("select * from tb_user WITH(UPDLOCK) where id = ?", id);
      return super.get(sql);
    }
}
