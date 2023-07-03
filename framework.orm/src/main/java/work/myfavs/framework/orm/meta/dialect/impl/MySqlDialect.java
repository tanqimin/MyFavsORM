package work.myfavs.framework.orm.meta.dialect.impl;

import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import java.util.Collection;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.DefaultDialect;
import work.myfavs.framework.orm.util.DruidUtil;

/**
 * @author tanqimin
 */
public class MySqlDialect extends DefaultDialect {

  @Override
  public String dbType() {

    return DbType.MYSQL;
  }

  @Override
  public Sql selectTop(int limit, String sql, Collection<?> params) {
    String querySql = limit(sql, 0, limit).toUnformattedString();
    return new Sql(querySql, params);
  }

  @Override
  public Sql selectPage(int currentPage, int pageSize, String sql, Collection<?> params) {
    int offset = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(this.dbType(), sql);
    SQLSelect select = selectStmt.getSelect();

    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();
    SQLLimit limit = queryBlock.getLimit();

    if (limit == null) {
      limit = new SQLLimit();
      queryBlock.setLimit(limit);
    }

    if (offset > 0) {
      limit.setOffset(new SQLIntegerExpr(offset));
    }

    limit.setRowCount(new SQLIntegerExpr(count));

    return selectStmt;
  }
}
