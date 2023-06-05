package work.myfavs.framework.orm.util;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import java.util.List;
import work.myfavs.framework.orm.util.exception.DBException;

public class DruidUtil {
  public static com.alibaba.druid.DbType convert(String dbType) {
    switch (dbType) {
      case work.myfavs.framework.orm.meta.DbType.SQL_SERVER:
      case work.myfavs.framework.orm.meta.DbType.SQL_SERVER_2012:
        return JdbcConstants.SQL_SERVER;
      case work.myfavs.framework.orm.meta.DbType.MYSQL:
        return JdbcConstants.MYSQL;
      case work.myfavs.framework.orm.meta.DbType.POSTGRE_SQL:
        return JdbcConstants.POSTGRESQL;
      case work.myfavs.framework.orm.meta.DbType.ORACLE:
        return JdbcConstants.ORACLE;
      case work.myfavs.framework.orm.meta.DbType.H2:
        return JdbcConstants.H2;
      default:
        throw new DBException("Unsupported database type: " + dbType);
    }
  }

  public static SQLSelectStatement createSQLSelectStatement(String dbType, String sql) {
    return createSQLSelectStatement(convert(dbType), sql);
  }

  public static SQLSelectStatement createSQLSelectStatement(DbType dbType, String sql) {
    List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
    if (stmtList.size() != 1) throw new DBException("Unsupported sql:" + sql);
    SQLStatement stmt = stmtList.get(0);
    if (!(stmt instanceof SQLSelectStatement)) throw new DBException("Unsupported sql:" + sql);
    return (SQLSelectStatement) stmt;
  }
}
