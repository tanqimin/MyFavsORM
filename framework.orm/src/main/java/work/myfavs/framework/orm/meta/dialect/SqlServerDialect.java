package work.myfavs.framework.orm.meta.dialect;

import java.util.List;
import java.util.regex.Matcher;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;


/**
 * @author tanqimin
 */
public class SqlServerDialect
    extends DefaultDialect {

  @Override
  public String getDialectName() {

    return DbType.SQL_SERVER;
  }

  @Override
  public Sql selectTop(long currentPage, long pageSize, String sql, List<Object> params) {

    if (currentPage == 1L && pageSize == 1L) {
      //如果sql本身只返回一个结果
      if (selectSinglePattern.matcher(sql).find()) {
        return new Sql(sql, params);
      }
    }

    if (pageSize == -1L) {
      return new Sql(sql, params);
    }

    long offset = pageSize * (currentPage - 1L);

    String  orderBys = null;
    Matcher om       = orderPattern.matcher(sql);
    if (om.find()) {
      orderBys = sql.substring(om.end(), sql.length());
      sql = sql.substring(0, om.start());
    }

    //mssql ROW_NUMBER分页必须要至少一个ORDER BY
    if (orderBys == null) {
      orderBys = "CURRENT_TIMESTAMP";
    }

    Sql querySql = new Sql();
    querySql.append("SELECT paginate_alias.* FROM (SELECT ROW_NUMBER() OVER (ORDER BY ");
    querySql.append(orderBys);
    querySql.append(") rownumber,");

    Matcher sm = selectPattern.matcher(sql);
    if (sm.find()) {
      querySql.append(sql.substring(sm.end()), params);
    } else {
      querySql.append(sql, params);
    }

    // T-SQL offset starts with 1, not like MySQL with 0;
    querySql.append(") paginate_alias WHERE rownumber BETWEEN ? AND ?", offset + 1L, pageSize + offset);

    return querySql;
  }

}
