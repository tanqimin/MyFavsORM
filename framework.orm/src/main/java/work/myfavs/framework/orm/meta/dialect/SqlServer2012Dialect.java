package work.myfavs.framework.orm.meta.dialect;


import java.util.Collection;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * @author tanqimin
 */
public class SqlServer2012Dialect
    extends DefaultDialect {

  @Override
  public String getDialectName() {

    return DbType.SQL_SERVER_2012;
  }

  @Override
  public Sql selectTop(int currentPage,
      int pageSize,
      String sql,
      Collection params) {

    Sql querySql = new Sql(sql, params);

    if (currentPage == 1 && pageSize == 1) {
      //如果sql本身只返回一个结果
      if (P_SELECT_SINGLE.matcher(sql)
          .find()) {
        return querySql;
      }
    }

    if (currentPage < 1 || pageSize < 1) {
      return querySql;
    }

    int offset = pageSize * (currentPage - 1);
    return querySql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY", offset, pageSize);
  }

}
