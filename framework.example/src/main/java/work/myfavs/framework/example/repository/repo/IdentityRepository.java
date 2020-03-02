package work.myfavs.framework.example.repository.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.example.domain.entity.Identity;
import work.myfavs.framework.example.repository.BaseRepository;
import work.myfavs.framework.orm.DBTemplate;

/**
 * Identity Repository
 * PS: 此文件通过代码生成器生成
 */
@Repository
public class IdentityRepository
    extends BaseRepository<Identity> {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public IdentityRepository(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

}