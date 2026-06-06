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

/**
 * Druid SQL 解析工具类，提供 SQL 语句的解析和构建功能
 */
public class DruidUtil {

  /**
   * 将框架数据库类型转换为 Druid 数据库类型
   *
   * @param dbType 框架定义的数据库类型字符串
   * @return Druid {@link DbType} 枚举
   * @throws work.myfavs.framework.orm.util.exception.DBException 不支持的数据库类型时抛出
   */
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

  /**
   * 根据数据库类型字符串创建 SQL SELECT 语句对象
   *
   * @param dbType 数据库类型字符串
   * @param sql    SQL 语句
   * @return {@link SQLSelectStatement}
   * @throws work.myfavs.framework.orm.util.exception.DBException SQL 语句不合法时抛出
   */
  public static SQLSelectStatement createSQLSelectStatement(String dbType, String sql) {
    return createSQLSelectStatement(convert(dbType), sql);
  }

  /**
   * 根据 Druid 数据库类型创建 SQL SELECT 语句对象
   *
   * @param dbType Druid {@link DbType}
   * @param sql    SQL 语句
   * @return {@link SQLSelectStatement}
   * @throws work.myfavs.framework.orm.util.exception.DBException SQL 语句不合法时抛出
   */
  public static SQLSelectStatement createSQLSelectStatement(DbType dbType, String sql) {
    List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
    if (stmtList.size() != 1) throw new DBException("不支持的Sql语句: %s", sql);
    SQLStatement stmt = stmtList.get(0);
    if (!(stmt instanceof SQLSelectStatement)) throw new DBException("不支持的Sql语句: %s", sql);
    return (SQLSelectStatement) stmt;
  }

  /**
   * 创建 SQL INSERT 语句对象
   *
   * @param tableName 表名
   * @return {@link SQLInsertStatement}
   */
  public static SQLInsertStatement createSQLInsertStatement(String tableName) {
    SQLInsertStatement insertStatement = new SQLInsertStatement();
    insertStatement.setTableSource(createTableSource(tableName));
    return insertStatement;
  }

  /**
   * 创建 SQL UPDATE 语句对象
   *
   * @param tableName 表名
   * @return {@link SQLUpdateStatement}
   */
  public static SQLUpdateStatement createSQLUpdateStatement(String tableName) {
    SQLUpdateStatement updateStatement = new SQLUpdateStatement();
    updateStatement.setTableSource(createTableSource(tableName));
    return updateStatement;
  }

  /**
   * 创建参数占位符表达式 {@code ?}
   *
   * @return {@link SQLVariantRefExpr}
   */
  public static SQLVariantRefExpr createParam() {
    return new SQLVariantRefExpr("?");
  }

  /**
   * 创建列标识符表达式
   *
   * @param columnName 列名
   * @return {@link SQLIdentifierExpr}
   */
  public static SQLIdentifierExpr createColumn(String columnName) {
    return new SQLIdentifierExpr(columnName);
  }

  /**
   * 创建 UPDATE SET 项，列为指定列名，值为参数占位符
   *
   * @param columnName 列名
   * @return {@link SQLUpdateSetItem}
   */
  public static SQLUpdateSetItem createUpdateSetItem(String columnName) {
    SQLUpdateSetItem sqlUpdateSetItem = new SQLUpdateSetItem();
    sqlUpdateSetItem.setColumn(DruidUtil.createColumn(columnName));
    sqlUpdateSetItem.setValue(DruidUtil.createParam());
    return sqlUpdateSetItem;
  }

  /**
   * 创建表源表达式
   *
   * @param tableName 表名
   * @return {@link SQLExprTableSource}
   */
  public static SQLExprTableSource createTableSource(String tableName) {
    return new SQLExprTableSource(tableName);
  }
}
