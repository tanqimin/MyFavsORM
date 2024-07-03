package work.myfavs.framework.orm.repository;

import work.myfavs.framework.orm.DBTemplate;

public abstract class SimpleRepository {
  protected DBTemplate dbTemplate;

  public SimpleRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
  }
}
