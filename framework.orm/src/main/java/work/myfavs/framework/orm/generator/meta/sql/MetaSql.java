package work.myfavs.framework.orm.generator.meta.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

public abstract class MetaSql {

  protected String dbName;

  public MetaSql(String dbName) {

    this.dbName = dbName;
  }

  public static Sql get(Connection conn) {

    MetaSql metaSql = null;
    try {
      DatabaseMetaData metaData = conn.getMetaData();
      String           prodName = metaData.getDatabaseProductName();
      boolean          mysql    = StringUtil.eq(prodName, "mysql", true);
      if (mysql) {
        metaSql = new MySqlMetaSql(conn.getCatalog());
      } else {
        throw new DBException(StringUtil.format("暂时不支持 {} 数据库", prodName));
      }
    } catch (SQLException e) {
      throw new DBException(e);
    }

    return metaSql.getSql();
  }

  public abstract Sql getSql();

}
