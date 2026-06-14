package work.myfavs.framework.orm.orm.dialect;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * SQL Server 2005~2008 方言实现。
 * <p>分页使用 ROW_NUMBER() OVER 语法；
 * UPSERT 使用 MERGE INTO ... USING (VALUES ...) AS source ... 语法。</p>
 *
 * <p><b>关于 ORDER BY 的约束：</b></p>
 * <p>SQL Server 的 {@code ROW_NUMBER()} 函数必须带 {@code ORDER BY} 子句。
 * 如果原始查询没有 {@code ORDER BY}，框架会回退使用 {@code ORDER BY CURRENT_TIMESTAMP} 使 SQL 合法，
 * 但此时结果的<em>排序顺序是非确定性的</em>（多次执行同一查询可能返回不同顺序）。
 * 建议分页查询始终提供确定的 {@code ORDER BY} 子句。</p>
 */
public class SqlServerDialect implements SqlDialect {

  public static final SqlServerDialect INSTANCE = new SqlServerDialect();

  private static final String COL_ROW_NUM = "_rn";
  private static final String TABLE_ALIAS = "_paginate";

  @Override
  public com.alibaba.druid.DbType getDruidDbType() {
    return JdbcConstants.SQL_SERVER;
  }

  @Override
  public Sql applyPageSql(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private static SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement       selectStmt = DruidUtil.createSQLSelectStatement(JdbcConstants.SQL_SERVER, sql);
    SQLSelect                select     = selectStmt.getSelect();
    SQLServerSelectQueryBlock queryBlock = (SQLServerSelectQueryBlock) select.getQuery();

    SQLSelectItem rowNumSelectItem = createRowNumberSQLSelectItem(queryBlock);
    queryBlock.getSelectList().add(rowNumSelectItem);

    SQLServerSelectQueryBlock countQueryBlock = new SQLServerSelectQueryBlock();
    countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
    countQueryBlock.setFrom(new SQLSubqueryTableSource(queryBlock.clone(), TABLE_ALIAS));
    countQueryBlock.setWhere(createBetweenExpr(offset, count));

    select.setQuery(countQueryBlock);
    return selectStmt;
  }

  private static SQLSelectItem createRowNumberSQLSelectItem(SQLServerSelectQueryBlock queryBlock) {
    SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
    aggregateExpr.setOver(createSQLOver(queryBlock));
    queryBlock.setOrderBy(null);
    return new SQLSelectItem(aggregateExpr, COL_ROW_NUM);
  }

  private static SQLOver createSQLOver(SQLServerSelectQueryBlock queryBlock) {
    if (null == queryBlock.getOrderBy())
      return new SQLOver(new SQLOrderBy(SQLUtils.toSQLExpr("CURRENT_TIMESTAMP")));
    return new SQLOver(queryBlock.getOrderBy());
  }

  private static SQLBetweenExpr createBetweenExpr(int offset, int count) {
    return new SQLBetweenExpr(
        new SQLIdentifierExpr(COL_ROW_NUM),
        new SQLNumberExpr(offset + 1),
        new SQLNumberExpr(count + offset));
  }

  @Override
  public String getUpsertSql(String tableName, List<String> columnNames, String pkColumn) {
    return getUpsertSql(tableName, columnNames, pkColumn, false);
  }

  @Override
  public String getUpsertSql(String tableName, List<String> columnNames, String pkColumn, boolean isIdentity) {
    final String colsJoined   = String.join(", ", columnNames);
    final String placeholders = String.join(", ", Collections.nCopies(columnNames.size(), "?"));

    final StringBuilder updatePart = new StringBuilder();
    final StringBuilder insertCols = new StringBuilder();
    final StringBuilder insertVals = new StringBuilder();
    boolean first = true;
    for (String col : columnNames) {
      if (isIdentity && col.equalsIgnoreCase(pkColumn)) {
        continue; // 自增主键不参与 INSERT/UPDATE 子句，但仍出现在 USING 中用于 ON 匹配
      }
      if (!first) {
        updatePart.append(", ");
        insertCols.append(", ");
        insertVals.append(", ");
      }
      updatePart.append(col).append(" = source.").append(col);
      insertCols.append(col);
      insertVals.append("source.").append(col);
      first = false;
    }

    final String outputClause = isIdentity ? " OUTPUT INSERTED." + pkColumn : "";

    if (updatePart.length() == 0) {
      // 仅 PK 列，无可更新字段 → 退化为仅 INSERT 的 MERGE（无 WHEN MATCHED 子句）
      // INSERT VALUES 使用 source.column 引用，避免额外参数
      final String insertValsRef = "source." + String.join(", source.", columnNames);
      return String.format(
          "MERGE INTO %s AS target USING (VALUES (%s)) AS source (%s) "
              + "ON (target.%s = source.%s) "
              + "WHEN NOT MATCHED THEN INSERT (%s) VALUES (%s)%s;",
          tableName, placeholders, colsJoined,
          pkColumn, pkColumn,
          colsJoined, insertValsRef, outputClause);
    }

    return String.format(
        "MERGE INTO %s AS target USING (VALUES (%s)) AS source (%s) "
            + "ON (target.%s = source.%s) "
            + "WHEN MATCHED THEN UPDATE SET %s "
            + "WHEN NOT MATCHED THEN INSERT (%s) VALUES (%s)%s;",
        tableName, placeholders, colsJoined,
        pkColumn, pkColumn,
        updatePart, insertCols, insertVals, outputClause);
  }
}
