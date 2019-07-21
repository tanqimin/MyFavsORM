package work.myfavs.framework.orm.repository;

import work.myfavs.framework.orm.DBTemplate;

abstract public class AbstractRepository {

  protected DBTemplate dbTemplate;

  private AbstractRepository() {}

  public AbstractRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
  }

}
