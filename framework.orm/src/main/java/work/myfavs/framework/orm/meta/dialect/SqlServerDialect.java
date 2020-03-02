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
  public Sql selectTop(int currentPage,
                       int pageSize,
                       String sql,
                       List<Object> params) {

    if (currentPage == 1 && pageSize == 1) {
      //如果sql本身只返回一个结果
      if (P_SELECT_SINGLE.matcher(sql)
                         .find()) {
        return new Sql(sql, params);
      }
    }

    if (currentPage < 1 || pageSize < 1) {
      return new Sql(sql, params);
    }

    int offset = pageSize * (currentPage - 1);

    String  orderBys = null;
    Matcher om       = P_ORDER.matcher(sql);
    if (om.find()) {
      orderBys = sql.substring(om.end());
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

    Matcher sm = P_SELECT.matcher(sql);
    if (sm.find()) {
      querySql.append(sql.substring(sm.end()), params);
    } else {
      querySql.append(sql, params);
    }

    // T-SQL offset starts with 1, not like MySQL with 0;
    return querySql.append(") paginate_alias WHERE rownumber BETWEEN ? AND ?", offset + 1L, pageSize + offset);
  }

}
