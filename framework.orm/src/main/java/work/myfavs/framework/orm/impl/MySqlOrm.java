package work.myfavs.framework.orm.impl;

import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;
import java.util.Objects;

public class MySqlOrm extends AbstractOrm {
  public MySqlOrm(Database database) {
    super(database);
  }

  @Override
  protected String dbType() {
    return DbType.MYSQL;
  }

  @Override
  protected Sql selectPage(String sql, Collection<?> params, int currentPage, int pageSize) {
    int    offset   = pageSize * (currentPage - 1);
    String querySql = limit(sql, offset, pageSize).toUnformattedString();
    return new Sql(querySql, params);
  }

  private SQLSelectStatement limit(String sql, int offset, int count) {
    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(this.dbType(), sql);
    SQLSelect          select     = selectStmt.getSelect();

    SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) select.getQuery();
    SQLLimit            sqlLimit   = queryBlock.getLimit();

    if (Objects.isNull(sqlLimit)) {
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
