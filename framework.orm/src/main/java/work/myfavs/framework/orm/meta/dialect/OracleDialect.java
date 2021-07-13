package work.myfavs.framework.orm.meta.dialect;


import java.util.Collection;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;

/**
 * @author tanqimin
 */
public class OracleDialect
    extends DefaultDialect {

  @Override
  public String getDialectName() {

    return DbType.ORACLE;
  }

  @Override
  public Sql selectTop(int currentPage,
      int pageSize,
      String sql,
      Collection params) {

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
    //"SELECT * FROM ( SELECT row_.*, ROWNUM rownum_ FROM (  " + sql + " ) row_ WHERE ROWNUM <= " + end + ") paginate_alias" + " WHERE paginate_alias.rownum_ >= " + start

    Sql querySql = new Sql();
    querySql.append("SELECT * FROM ( SELECT row_.*, ROWNUM rownum_ FROM (");
    querySql.append(sql, params);
    querySql.append(") row_ WHERE ROWNUM <= ? ) paginate_alias", pageSize + offset);
    querySql.append(" WHERE paginate_alias.rownum_ >= ?", offset + 1L);

    return querySql;
  }

}
