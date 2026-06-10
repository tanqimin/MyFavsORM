package work.myfavs.framework.orm.orm.dialect;

import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * PostgreSQL 方言实现。
 * <p>分页使用 LIMIT ... OFFSET 语法（兼容 MySQL）；
 * UPSERT 使用 INSERT ... ON CONFLICT (pk) DO UPDATE SET ... 语法。</p>
 */
public class PostgreSqlDialect implements SqlDialect {

  public static final PostgreSqlDialect INSTANCE = new PostgreSqlDialect();

  @Override
  public com.alibaba.druid.DbType getDruidDbType() {
    return JdbcConstants.POSTGRESQL;
  }

  @Override
  public Sql applyPageSql(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private static SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement  selectStmt = DruidUtil.createSQLSelectStatement(JdbcConstants.POSTGRESQL, sql);
    SQLSelect           select     = selectStmt.getSelect();
    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();
    SQLLimit            sqlLimit   = queryBlock.getLimit();

    if (null == sqlLimit) {
      sqlLimit = new SQLLimit();
      queryBlock.setLimit(sqlLimit);
    }
    if (offset > 0) {
      sqlLimit.setOffset(new SQLIntegerExpr(offset));
    }
    sqlLimit.setRowCount(new SQLIntegerExpr(count));
    return selectStmt;
  }

  @Override
  public String getUpsertSql(String tableName, List<String> columnNames, String pkColumn) {
    final String colsJoined   = String.join(", ", columnNames);
    final String placeholders = String.join(", ", Collections.nCopies(columnNames.size(), "?"));

    final StringBuilder updatePart = new StringBuilder();
    boolean first = true;
    for (String col : columnNames) {
      if (col.equalsIgnoreCase(pkColumn)) {
        continue;
      }
      if (!first) {
        updatePart.append(", ");
      }
      updatePart.append(col).append(" = EXCLUDED.").append(col);
      first = false;
    }
    if (updatePart.length() == 0) {
      // 仅 PK 列，无可更新字段 → 退化为普通 INSERT
      return String.format(
          "INSERT INTO %s (%s) VALUES (%s)",
          tableName, colsJoined, placeholders);
    }
    return String.format(
        "INSERT INTO %s (%s) VALUES (%s) ON CONFLICT (%s) DO UPDATE SET %s;",
        tableName, colsJoined, placeholders, pkColumn, updatePart);
  }
}
