package work.myfavs.framework.orm;

import java.sql.Connection;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;

public class DBTemplate
    extends Orm {

  protected DBTemplate() {

  }

  public DBTemplate(DataSource dataSource) {

    super(dataSource);
  }

  @Override
  public Database open() {

    return new Database(this);
  }

  /**
   * 创建一个 DBTemplate 实例
   *
   * @param dataSource 数据源
   *
   * @return DBTemplate 实例
   */
  public static DBTemplate build(DataSource dataSource) {

    return new DBTemplate(dataSource);
  }

  @Override
  public Connection getCurrentConnection() {

    return DataSourceUtils.getConnection(this.getDataSource());
  }

  @Override
  public void release() {

    DataSourceUtils.releaseConnection(getCurrentConnection(), getDataSource());
  }

  @Override
  public void commit() {

  }

  @Override
  public void rollback() {

  }

  @Override
  public DBTemplate registerPropertyHandler(Class<?> clazz, PropertyHandler propertyHandler) {

    super.registerPropertyHandler(clazz, propertyHandler);
    return this;
  }

  @Override
  public DBTemplate registerDefaultPropertyHandler() {

    super.registerDefaultPropertyHandler();
    return this;
  }

  @Override
  public DBTemplate setDataSource(DataSource dataSource) {

    super.setDataSource(dataSource);
    return this;
  }

  @Override
  public DBTemplate setDbType(String dbType) {

    super.setDbType(dbType);
    return this;
  }

  @Override
  public DBTemplate setBatchSize(int batchSize) {

    super.setBatchSize(batchSize);
    return this;
  }

  @Override
  public DBTemplate setFetchSize(int fetchSize) {

    super.setFetchSize(fetchSize);
    return this;
  }

  @Override
  public DBTemplate setQueryTimeout(int queryTimeout) {

    super.setQueryTimeout(queryTimeout);
    return this;
  }

  @Override
  public DBTemplate setShowSql(boolean showSql) {

    super.setShowSql(showSql);
    return this;
  }

  @Override
  public DBTemplate setShowResult(boolean showResult) {

    super.setShowResult(showResult);
    return this;
  }

  @Override
  public DBTemplate setMaxPageSize(int maxPageSize) {

    super.setMaxPageSize(maxPageSize);
    return this;
  }

  @Override
  public Orm setDefaultIsolation(int defaultIsolation) {

    super.setDefaultIsolation(defaultIsolation);
    return this;
  }

}
