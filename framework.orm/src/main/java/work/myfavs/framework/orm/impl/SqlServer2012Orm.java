package work.myfavs.framework.orm.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;
import java.util.Objects;

public class SqlServer2012Orm extends SqlServerOrm {
  public SqlServer2012Orm(Database database) {
    super(database);
  }

  @Override
  protected String dbType() {
    return DbType.SQL_SERVER_2012;
  }

  @Override
  protected Sql selectPage(String sql, Collection<?> params, int currentPage, int pageSize) {
    int offset = pageSize * (currentPage - 1);

    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(this.dbType(), sql);
    SQLServerSelectQueryBlock queryBlock =
        (SQLServerSelectQueryBlock) selectStmt.getSelect().getQuery();

    if (Objects.isNull(queryBlock.getOrderBy())) {
      queryBlock.setOrderBy(new SQLOrderBy(SQLUtils.toSQLExpr("CURRENT_TIMESTAMP")));
    }

    return new Sql(selectStmt.toUnformattedString(), params)
        .append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", offset, pageSize);
  }
}
