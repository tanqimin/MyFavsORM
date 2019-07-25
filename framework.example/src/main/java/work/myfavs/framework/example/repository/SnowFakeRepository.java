package work.myfavs.framework.example.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import work.myfavs.framework.example.domain.entity.TestSnowFake;
import work.myfavs.framework.orm.DBTemplate;

@org.springframework.stereotype.Repository
public class SnowFakeRepository
    extends BaseRepository<TestSnowFake> {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public SnowFakeRepository(@Qualifier("primaryDBTemplate") DBTemplate dbTemplate) {

    super(dbTemplate);
  }

}
