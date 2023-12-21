package work.myfavs.framework.orm;

import work.myfavs.framework.orm.impl.*;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.util.exception.DBException;

public class OrmFactory {

  public static Orm createOrm(Database database) {
    String dbType = database.getDbConfig().getDbType();
    switch (dbType) {
      case DbType.SQL_SERVER:
        return new SqlServerOrm(database);
      case DbType.SQL_SERVER_2012:
        return new SqlServer2012Orm(database);
      case DbType.MYSQL:
        return new MySqlOrm(database);
      case DbType.POSTGRE_SQL:
        return new PostgreSQLOrm(database);
      case DbType.ORACLE:
        return new OracleOrm(database);
      case DbType.H2:
        return new H2Orm(database);
      default:
        throw new DBException("%s database is not supported.", dbType);
    }
  }
}
