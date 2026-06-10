package work.myfavs.framework.orm.orm.dialect;

import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;
import java.util.List;

/**
 * Oracle 方言实现。
 * <p>分页使用 ROWNUM 双层子查询；
 * UPSERT 使用 MERGE INTO ... USING (SELECT ... FROM DUAL) source ... 语法。</p>
 */
public class OracleDialect implements SqlDialect {

  public static final OracleDialect INSTANCE = new OracleDialect();

  private static final String INNER_TABLE_ALIAS = "_limit";
  private static final String OUTER_TABLE_ALIAS = "_paginate";
  private static final String COL_ROW_NUM       = "_rn";

  @Override
  public com.alibaba.druid.DbType getDruidDbType() {
    return JdbcConstants.ORACLE;
  }

  @Override
  public Sql applyPageSql(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private static SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(JdbcConstants.ORACLE, sql);
    SQLSelect          select     = selectStmt.getSelect();

    int                    maxRow     = count + offset;
    OracleSelectQueryBlock innerQuery = createInnerQuery(select, maxRow);

    select.setOrderBy(null);
    if (offset <= 0) {
      select.setQuery(innerQuery);
      return selectStmt;
    }

    OracleSelectQueryBlock outerQuery = createOuterQuery(innerQuery, offset);
    select.setQuery(outerQuery);
    return selectStmt;
  }

  private static OracleSelectQueryBlock createOuterQuery(OracleSelectQueryBlock innerQuery, int offset) {
    OracleSelectQueryBlock outerQuery = new OracleSelectQueryBlock();
    outerQuery.getSelectList().add(createSelectAllItem(OUTER_TABLE_ALIAS));
    outerQuery.setFrom(new SQLSubqueryTableSource(new SQLSelect(innerQuery), OUTER_TABLE_ALIAS));
    outerQuery.setWhere(createGtCondition(offset));
    return outerQuery;
  }

  private static OracleSelectQueryBlock createInnerQuery(SQLSelect select, int maxRow) {
    OracleSelectQueryBlock innerQuery = new OracleSelectQueryBlock();
    innerQuery.getSelectList().add(createSelectAllItem(INNER_TABLE_ALIAS));
    innerQuery.getSelectList().add(new SQLSelectItem(new SQLIdentifierExpr("ROWNUM"), COL_ROW_NUM));
    innerQuery.setFrom(new SQLSubqueryTableSource(select.clone(), INNER_TABLE_ALIAS));
    innerQuery.setWhere(createLteqCondition(maxRow));
    return innerQuery;
  }

  private static SQLSelectItem createSelectAllItem(String alias) {
    return new SQLSelectItem(new SQLPropertyExpr(new SQLIdentifierExpr(alias), "*"));
  }

  private static SQLBinaryOpExpr createLteqCondition(int count) {
    return new SQLBinaryOpExpr(
        new SQLPropertyExpr(new SQLIdentifierExpr(INNER_TABLE_ALIAS), "ROWNUM"),
        SQLBinaryOperator.LessThanOrEqual,
        new SQLNumberExpr(count),
        JdbcConstants.ORACLE);
  }

  private static SQLBinaryOpExpr createGtCondition(int offset) {
    return new SQLBinaryOpExpr(
        new SQLPropertyExpr(new SQLIdentifierExpr(OUTER_TABLE_ALIAS), COL_ROW_NUM),
        SQLBinaryOperator.GreaterThan,
        new SQLNumberExpr(offset),
        JdbcConstants.ORACLE);
  }

  @Override
  public String getUpsertSql(String tableName, List<String> columnNames, String pkColumn) {
    final StringBuilder selectClause = new StringBuilder("SELECT ");
    final StringBuilder updatePart   = new StringBuilder();
    final StringBuilder insertPart   = new StringBuilder();
    boolean first = true;
    for (String col : columnNames) {
      if (!first) {
        selectClause.append(", ");
        updatePart.append(", ");
        insertPart.append(", ");
      }
      selectClause.append("? AS ").append(col);
      updatePart.append(col).append(" = source.").append(col);
      insertPart.append("source.").append(col);
      first = false;
    }
    selectClause.append(" FROM DUAL");

    final String colsJoined = String.join(", ", columnNames);

    if (updatePart.length() == 0) {
      // 仅 PK 列，无可更新字段 → 退化为仅 INSERT 的 MERGE
      return String.format(
          "MERGE INTO %s target USING (%s) source ON (target.%s = source.%s) "
              + "WHEN NOT MATCHED THEN INSERT (%s) VALUES (%s);",
          tableName, selectClause,
          pkColumn, pkColumn,
          colsJoined, insertPart);
    }

    return String.format(
        "MERGE INTO %s target USING (%s) source ON (target.%s = source.%s) "
            + "WHEN MATCHED THEN UPDATE SET %s "
            + "WHEN NOT MATCHED THEN INSERT (%s) VALUES (%s);",
        tableName, selectClause,
        pkColumn, pkColumn,
        updatePart, colsJoined, insertPart);
  }
}
