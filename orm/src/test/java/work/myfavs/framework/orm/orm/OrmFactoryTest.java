package work.myfavs.framework.orm.orm;

import org.junit.Test;
import org.mockito.Mockito;
import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.orm.impl.*;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;

import static org.junit.Assert.*;

public class OrmFactoryTest {

  @Test
  public void dispatch_ShouldReturnMySqlOrm_WhenDbTypeIsMysql() {
    assertTrue(dispatch(DbType.MYSQL) instanceof MySqlOrm);
  }

  @Test
  public void dispatch_ShouldReturnSqlServerOrm_WhenDbTypeIsSqlServer() {
    assertTrue(dispatch(DbType.SQL_SERVER) instanceof SqlServerOrm);
  }

  @Test
  public void dispatch_ShouldReturnSqlServer2012Orm_WhenDbTypeIsSqlServer2012() {
    assertTrue(dispatch(DbType.SQL_SERVER_2012) instanceof SqlServer2012Orm);
  }

  @Test
  public void dispatch_ShouldReturnOracleOrm_WhenDbTypeIsOracle() {
    assertTrue(dispatch(DbType.ORACLE) instanceof OracleOrm);
  }

  @Test
  public void dispatch_ShouldReturnPostgreSQLOrm_WhenDbTypeIsPostgreSql() {
    assertTrue(dispatch(DbType.POSTGRE_SQL) instanceof PostgreSQLOrm);
  }

  @Test
  public void dispatch_ShouldReturnH2Orm_WhenDbTypeIsH2() {
    assertTrue(dispatch(DbType.H2) instanceof H2Orm);
  }

  @Test(expected = InvalidDataAccessException.class)
  public void dispatch_ShouldThrow_WhenDbTypeIsUnsupported() {
    OrmFactory.createOrm(mockDatabase("unsupported_db"));
  }

  private static Orm dispatch(String dbType) {
    return OrmFactory.createOrm(mockDatabase(dbType));
  }

  /**
   * 通过 Mockito 创建模拟 Database，避免依赖真实数据库连接。
   */
  private static Database mockDatabase(String dbType) {
    DBConfig config = new DBConfig();
    config.setDbType(dbType);

    DBTemplate template = Mockito.mock(DBTemplate.class);
    Mockito.when(template.getDbConfig()).thenReturn(config);

    Database database = Mockito.mock(Database.class);
    Mockito.when(database.getDbTemplate()).thenReturn(template);
    Mockito.when(database.getDbConfig()).thenReturn(config);

    return database;
  }
}
