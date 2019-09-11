package work.myfavs.framework.example.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.repository.Repository;

/**
 * Repository 基类
 * PS: 此文件通过代码生成器生成
 *
 * @param <TModel> 实体类泛型
 */
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