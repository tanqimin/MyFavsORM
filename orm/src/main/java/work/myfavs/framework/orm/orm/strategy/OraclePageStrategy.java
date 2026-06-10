package work.myfavs.framework.orm.orm.strategy;

import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;

/**
 * Oracle 分页策略：使用 ROWNUM 双层子查询。
 * <p>通过 Druid AST 构建内层子查询限制最大行号（{@code ROWNUM <= maxRow}），
 * 外层子查询过滤偏移量（{@code _rn > offset}）。适用于 Oracle 9i+。</p>
 *
 * @see MySqlPageStrategy
 * @see SqlServerPageStrategy
 * @see SqlServer2012PageStrategy
 */
public class OraclePageStrategy implements PageStrategy {

  public static final OraclePageStrategy INSTANCE = new OraclePageStrategy();

  private static final String INNER_TABLE_ALIAS = "_limit";
  private static final String OUTER_TABLE_ALIAS = "_paginate";
  private static final String COL_ROW_NUM       = "_rn";

  @Override
  public Sql apply(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private static SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(DbType.ORACLE, sql);
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

  private static OracleSelectQueryBlock createOuterQuery(
      OracleSelectQueryBlock innerQuery, int offset) {
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
}
