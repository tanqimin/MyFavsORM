package work.myfavs.framework.orm.orm.strategy;

import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;

/**
 * MySQL 分页策略：使用 LIMIT offset, count 语法。
 * <p>适用于 MySQL 5.0+ 和 H2 数据库。</p>
 *
 * @see OraclePageStrategy
 * @see SqlServerPageStrategy
 * @see SqlServer2012PageStrategy
 */
public class MySqlPageStrategy implements PageStrategy {

  public static final MySqlPageStrategy INSTANCE = new MySqlPageStrategy();

  @Override
  public Sql apply(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private static SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(DbType.MYSQL, sql);
    SQLSelect          select     = selectStmt.getSelect();

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
}
