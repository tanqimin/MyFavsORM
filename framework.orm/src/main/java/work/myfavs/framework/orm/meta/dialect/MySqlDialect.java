package work.myfavs.framework.orm.meta.dialect;


import java.util.List;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * @author tanqimin
 */
public class MySqlDialect
    extends DefaultDialect {

  @Override
  public String getDialectName() {

    return DbType.MYSQL;
  }

  @Override
  public Sql selectTop(long currentPage, long pageSize, String sql, List<Object> params) {

    Sql querySql;
    querySql = new Sql(sql, params);
    if (currentPage == 1L && pageSize == 1L) {
      //如果sql本身只返回一个结果
      if (selectSinglePattern.matcher(sql).find()) {
        return querySql;
      }
    }

    if (pageSize == -1L) {
      return querySql;
    }

    long offset = pageSize * (currentPage - 1L);
    return querySql.append(" LIMIT ? OFFSET ?", pageSize, offset);
  }

}
