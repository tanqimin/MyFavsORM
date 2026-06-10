package work.myfavs.framework.orm.orm.strategy;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOver;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;

/**
 * SQL Server 2005~2008 分页策略：使用 ROW_NUMBER() OVER 语法。
 * <p>通过 Druid AST 在原 SQL 中内嵌 {@code ROW_NUMBER() OVER (ORDER BY ...)} 窗口函数，
 * 外层按行号范围 {@code BETWEEN offset+1 AND count+offset} 过滤。适用于 SQL Server 2005~2008。</p>
 * <p>若使用 SQL Server 2012+，请改用 {@link SqlServer2012PageStrategy}。</p>
 *
 * @see SqlServer2012PageStrategy
 * @see MySqlPageStrategy
 * @see OraclePageStrategy
 */
public class SqlServerPageStrategy implements PageStrategy {

  public static final SqlServerPageStrategy INSTANCE = new SqlServerPageStrategy();

  private static final String COL_ROW_NUM = "_rn";
  private static final String TABLE_ALIAS = "_paginate";

  @Override
  public Sql apply(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private static SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(DbType.SQL_SERVER, sql);
    SQLSelect          select     = selectStmt.getSelect();

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
}
