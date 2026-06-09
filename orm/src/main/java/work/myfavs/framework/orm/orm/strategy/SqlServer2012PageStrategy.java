package work.myfavs.framework.orm.orm.strategy;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;

/**
 * SQL Server 2012+ 分页策略：使用 OFFSET...FETCH NEXT 语法
 */
public class SqlServer2012PageStrategy implements PageStrategy {

  public static final SqlServer2012PageStrategy INSTANCE = new SqlServer2012PageStrategy();

  @Override
  public Sql apply(String sql, Collection<?> params, int currentPage, int pageSize) {
    int offset = pageSize * (currentPage - 1);

    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(DbType.SQL_SERVER_2012, sql);
    SQLServerSelectQueryBlock queryBlock =
        (SQLServerSelectQueryBlock) selectStmt.getSelect().getQuery();

    if (null == queryBlock.getOrderBy()) {
      queryBlock.setOrderBy(new SQLOrderBy(SQLUtils.toSQLExpr("CURRENT_TIMESTAMP")));
    }

    SQLLimit sqlLimit = new SQLLimit(new SQLIntegerExpr(offset), new SQLIntegerExpr(pageSize));
    sqlLimit.setOffsetClause(true);
    queryBlock.setLimit(sqlLimit);

    return new Sql(selectStmt.toUnformattedString(), params);
  }
}
