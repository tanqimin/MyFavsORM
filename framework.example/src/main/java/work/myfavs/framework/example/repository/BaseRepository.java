package work.myfavs.framework.example.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Repository;

public class BaseRepository<TModel>
    extends Repository<TModel> {

  /**
   * 构造方法
   *
   * @param dbTemplate DBTemplate
   */
  public BaseRepository(DBTemplate dbTemplate) {

    super(dbTemplate);
  }

}