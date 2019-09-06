package work.myfavs.framework.example.repository.repo;

import work.myfavs.framework.example.repository.BaseRepository;
import work.myfavs.framework.example.domain.entity.Snowfake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.orm.DBTemplate;

/**
 * Snowfake Repository
 * PS: 此文件通过代码生成器生成
 */
@Repository
public class SnowfakeRepository extends BaseRepository<Snowfake> {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public SnowfakeRepository(DBTemplate dbTemplate) {

    super(dbTemplate);
  }
}