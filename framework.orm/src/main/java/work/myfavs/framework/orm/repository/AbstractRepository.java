package work.myfavs.framework.orm.repository;

import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.dialect.IDialect;

abstract public class AbstractRepository {

  protected IDialect   dialect;
  protected DBTemplate dbTemplate;

  private AbstractRepository() {}

  public AbstractRepository(DBTemplate dbTemplate) {

    this.dbTemplate = dbTemplate;
    dialect = dbTemplate.getDialect();
  }

}
