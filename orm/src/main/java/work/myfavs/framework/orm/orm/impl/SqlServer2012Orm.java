package work.myfavs.framework.orm.orm.impl;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.common.DruidUtil;

import java.util.Collection;

/**
 * Orm SqlServer实现 2012或以上版本
 */
public class SqlServer2012Orm extends SqlServerOrm {
  /**
   * 构造 SqlServer2012Orm 实例.
   *
   * @param database {@link Database} 实例
   */
  public SqlServer2012Orm(Database database) {
    super(database);
  }

  /**
   * 获取数据库类型.
   *
   * @return 数据库类型 {@link DbType#SQL_SERVER_2012}
   */
  @Override
  protected String dbType() {
    return DbType.SQL_SERVER_2012;
  }

  /**
   * 生成 SQL Server 2012+ 分页查询 {@link Sql}，使用 {@code OFFSET...FETCH} 语法.
   *
   * @param sql         原始 SQL
   * @param params      SQL 参数集合
   * @param currentPage 当前页码
   * @param pageSize    每页大小
   * @return 分页查询 {@link Sql}
   */
  @Override
  protected Sql selectPage(String sql, Collection<?> params, int currentPage, int pageSize) {
    int offset = pageSize * (currentPage - 1);

    SQLSelectStatement selectStmt = DruidUtil.createSQLSelectStatement(this.dbType(), sql);
    SQLServerSelectQueryBlock queryBlock =
        (SQLServerSelectQueryBlock) selectStmt.getSelect().getQuery();

    if (null == queryBlock.getOrderBy()) {
      queryBlock.setOrderBy(new SQLOrderBy(SQLUtils.toSQLExpr("CURRENT_TIMESTAMP")));
    }

    return new Sql(selectStmt.toUnformattedString(), params)
        .append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", offset, pageSize);
  }
}
