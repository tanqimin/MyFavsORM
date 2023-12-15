package work.myfavs.framework.orm.meta.dialect.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.util.JdbcConstants;

import java.util.Collection;

import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.DefaultDialect;
import work.myfavs.framework.orm.util.DruidUtil;

/**
 * @author tanqimin
 */
public class SqlServerDialect extends DefaultDialect {

  private static final String COL_ROW_NUM = "_rn";
  private static final String TABLE_ALIAS = "_paginate";

  public SqlServerDialect(DBConfig dbConfig) {
    super(dbConfig);
  }

  @Override
  public String dbType() {

    return DbType.SQL_SERVER;
  }

  @Override
  public Sql selectTop(int limit, String sql, Collection<?> params) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(this.dbType(), sql);
    SQLServerSelectQueryBlock queryBlock =
        (SQLServerSelectQueryBlock) selectStmt.getSelect().getQuery();
    queryBlock.setTop(new SQLServerTop(new SQLNumberExpr(limit)));
    return new Sql(selectStmt.toUnformattedString(), params);
  }

  @Override
  public Sql selectPage(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(this.dbType(), sql);
    SQLSelect          select     = selectStmt.getSelect();

    SQLServerSelectQueryBlock queryBlock = (SQLServerSelectQueryBlock) select.getQuery();

    // 构建 ROW_NUMBER() OVER (ORDER BY ...) AS _rn 列
    SQLSelectItem rowNumSelectItem = createRowNumberSQLSelectItem(queryBlock);
    queryBlock.getSelectList().add(rowNumSelectItem);

    // 构建外层查询语句
    SQLServerSelectQueryBlock countQueryBlock = new SQLServerSelectQueryBlock();
    countQueryBlock.getSelectList().add(new SQLSelectItem(new SQLAllColumnExpr()));
    countQueryBlock.setFrom(new SQLSubqueryTableSource(queryBlock.clone(), TABLE_ALIAS));
    countQueryBlock.setWhere(createBetweenExpr(offset, count));

    select.setQuery(countQueryBlock);
    return selectStmt;
  }

  private SQLSelectItem createRowNumberSQLSelectItem(SQLServerSelectQueryBlock queryBlock) {
    SQLAggregateExpr aggregateExpr = new SQLAggregateExpr("ROW_NUMBER");
    aggregateExpr.setOver(createSQLOver(queryBlock));
    queryBlock.setOrderBy(null);
    return new SQLSelectItem(aggregateExpr, COL_ROW_NUM);
  }

  private SQLOver createSQLOver(SQLServerSelectQueryBlock queryBlock) {
    if (queryBlock.getOrderBy() == null)
      return new SQLOver(new SQLOrderBy(SQLUtils.toSQLExpr("CURRENT_TIMESTAMP")));
    return new SQLOver(queryBlock.getOrderBy());
  }

  private static SQLBetweenExpr createBetweenExpr(int offset, int count) {
    return new SQLBetweenExpr(
        new SQLIdentifierExpr(COL_ROW_NUM),
        new SQLNumberExpr(offset + 1),
        new SQLNumberExpr(count + offset));
  }

  private static SQLBinaryOpExpr createPageCondition(int offset, int count) {
    SQLBinaryOpExpr gt =
        new SQLBinaryOpExpr(
            new SQLIdentifierExpr(COL_ROW_NUM),
            SQLBinaryOperator.GreaterThan,
            new SQLNumberExpr(offset),
            JdbcConstants.SQL_SERVER);
    SQLBinaryOpExpr lteq =
        new SQLBinaryOpExpr(
            new SQLIdentifierExpr(COL_ROW_NUM),
            SQLBinaryOperator.LessThanOrEqual,
            new SQLNumberExpr(count + offset),
            JdbcConstants.SQL_SERVER);
    return new SQLBinaryOpExpr(gt, SQLBinaryOperator.BooleanAnd, lteq, JdbcConstants.SQL_SERVER);
  }
}
