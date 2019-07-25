package work.myfavs.framework.example.repository.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import work.myfavs.framework.orm.DBTemplate;

@Repository
public class IdentityQuery
    extends BaseQuery {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  @Autowired
  public IdentityQuery(@Qualifier("primaryDBTemplate") DBTemplate dbTemplate) {

    super(dbTemplate);
  }

}
