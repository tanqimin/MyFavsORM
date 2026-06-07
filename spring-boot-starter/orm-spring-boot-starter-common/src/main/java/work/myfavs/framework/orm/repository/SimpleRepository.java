package work.myfavs.framework.orm.repository;

import work.myfavs.framework.orm.DBTemplate;

/**
 * 简单仓储基类，持有 {@link DBTemplate} 实例.
 * <p>所有仓储类的根基类，提供 {@link DBTemplate} 的构造注入.</p>
 *
 * @see DBTemplate
 * @see BaseRepository
 * @author tanqimin
 */
public abstract class SimpleRepository {
  protected DBTemplate dbTemplate;

  /**
   * 构造方法.
   *
   * @param dbTemplate {@link DBTemplate} 实例
   */
  public SimpleRepository(DBTemplate dbTemplate) {
    this.dbTemplate = dbTemplate;
  }
}
