package work.myfavs.framework.orm.util.common;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.JdbcConstants;
import work.myfavs.framework.orm.util.exception.DBException;

import java.util.List;

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
        throw new DBException("不支持的数据库类型: %s", dbType);
    }
  }

  public static SQLSelectStatement createSQLSelectStatement(String dbType, String sql) {
    return createSQLSelectStatement(convert(dbType), sql);
  }

  public static SQLSelectStatement createSQLSelectStatement(DbType dbType, String sql) {
    List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
    if (stmtList.size() != 1) throw new DBException("不支持的Sql语句: %s", sql);
    SQLStatement stmt = stmtList.get(0);
    if (!(stmt instanceof SQLSelectStatement)) throw new DBException("不支持的Sql语句: %s", sql);
    return (SQLSelectStatement) stmt;
  }

  public static SQLInsertStatement createSQLInsertStatement(String tableName) {
    SQLInsertStatement insertStatement = new SQLInsertStatement();
    insertStatement.setTableSource(createTableSource(tableName));
    return insertStatement;
  }

  public static SQLUpdateStatement createSQLUpdateStatement(String tableName) {
    SQLUpdateStatement updateStatement = new SQLUpdateStatement();
    updateStatement.setTableSource(createTableSource(tableName));
    return updateStatement;
  }

  public static SQLVariantRefExpr createParam() {
    return new SQLVariantRefExpr("?");
  }

  public static SQLIdentifierExpr createColumn(String columnName) {
    return new SQLIdentifierExpr(columnName);
  }

  public static SQLUpdateSetItem createUpdateSetItem(String columnName) {
    SQLUpdateSetItem sqlUpdateSetItem = new SQLUpdateSetItem();
    sqlUpdateSetItem.setColumn(DruidUtil.createColumn(columnName));
    sqlUpdateSetItem.setValue(DruidUtil.createParam());
    return sqlUpdateSetItem;
  }

  public static SQLExprTableSource createTableSource(String tableName) {
    return new SQLExprTableSource(tableName);
  }
}
