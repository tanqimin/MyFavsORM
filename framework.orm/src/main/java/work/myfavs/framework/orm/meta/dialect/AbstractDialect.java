package work.myfavs.framework.orm.meta.dialect;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.List;

public abstract class AbstractDialect {
  /**
   * 获取数据库方言名称
   *
   * @return 数据库方言名称
   */
  public abstract String getDialectName();

  protected com.alibaba.druid.DbType getDruidDbType() {
    switch (getDialectName()) {
      case DbType.MYSQL:
        return JdbcConstants.MYSQL;
      case DbType.SQL_SERVER:
      case DbType.SQL_SERVER_2012:
        return JdbcConstants.SQL_SERVER;
      case DbType.ORACLE:
        return JdbcConstants.ORACLE;
      case DbType.POSTGRE_SQL:
        return JdbcConstants.POSTGRESQL;
      case DbType.H2:
        return JdbcConstants.H2;
      default:
        throw new DBException("Unsupported database type: " + getDialectName());
    }
  }

  protected SQLSelectStatement createSQLSelectStatement(String sql) {
    List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, getDruidDbType());
    if (stmtList.size() != 1) throw new DBException("Unsupported sql:" + sql);
    SQLStatement stmt = stmtList.get(0);
    if (!(stmt instanceof SQLSelectStatement)) throw new DBException("Unsupported sql:" + sql);
    return (SQLSelectStatement) stmt;
  }
}
