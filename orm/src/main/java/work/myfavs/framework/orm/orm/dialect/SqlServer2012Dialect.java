package work.myfavs.framework.orm.orm.dialect;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.util.JdbcConstants;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;
import java.util.List;

/**
 * SQL Server 2012+ 方言实现。
 * <p>分页使用 OFFSET ... FETCH NEXT 语法；
 * UPSERT 使用 MERGE INTO ... USING (VALUES ...) AS source ... 语法（与 2005~2008 通用）。</p>
 */
public class SqlServer2012Dialect implements SqlDialect {

  public static final SqlServer2012Dialect INSTANCE = new SqlServer2012Dialect();

  @Override
  public com.alibaba.druid.DbType getDruidDbType() {
    return JdbcConstants.SQL_SERVER;
  }

  @Override
  public Sql applyPageSql(String sql, Collection<?> params, int currentPage, int pageSize) {
    int offset = pageSize * (currentPage - 1);

    SQLSelectStatement        selectStmt = DruidUtil.createSQLSelectStatement(JdbcConstants.SQL_SERVER, sql);
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

  @Override
  public String getUpsertSql(String tableName, List<String> columnNames, String pkColumn) {
    return SqlServerDialect.INSTANCE.getUpsertSql(tableName, columnNames, pkColumn);
  }
}
