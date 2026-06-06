package work.myfavs.framework.orm.orm.impl;

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

/**
 * Orm MySql实现
 */
public class MySqlOrm extends AbstractOrm {
  /**
   * 构造 MySqlOrm 实例.
   *
   * @param database {@link Database} 实例
   */
  public MySqlOrm(Database database) {
    super(database);
  }

  /**
   * 获取数据库类型.
   *
   * @return 数据库类型 {@link DbType#MYSQL}
   */
  @Override
  protected String dbType() {
    return DbType.MYSQL;
  }

  /**
   * 生成 MySQL 分页查询 {@link Sql}.
   *
   * @param sql         原始 SQL
   * @param params      SQL 参数集合
   * @param currentPage 当前页码
   * @param pageSize    每页大小
   * @return 分页查询 {@link Sql}
   */
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
