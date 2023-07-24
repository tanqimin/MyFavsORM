package work.myfavs.framework.orm.meta.dialect.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import java.util.Collection;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.DruidUtil;

/**
 * @author tanqimin
 */
public class SqlServer2012Dialect extends SqlServerDialect {

  @Override
  public String dbType() {

    return DbType.SQL_SERVER_2012;
  }

  @Override
  public Sql selectPage(int currentPage, int pageSize, String sql, Collection<?> params) {
    int offset = pageSize * (currentPage - 1);

    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(this.dbType(), sql);
    SQLServerSelectQueryBlock queryBlock =
        (SQLServerSelectQueryBlock) selectStmt.getSelect().getQuery();

    if (queryBlock.getOrderBy() == null) {
      queryBlock.setOrderBy(new SQLOrderBy(SQLUtils.toSQLExpr("CURRENT_TIMESTAMP")));
    }

    return new Sql(selectStmt.toUnformattedString(), params)
        .append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", offset, pageSize);
  }
}
