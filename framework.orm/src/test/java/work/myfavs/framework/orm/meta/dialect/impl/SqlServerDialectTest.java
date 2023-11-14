package work.myfavs.framework.orm.meta.dialect.impl;

import cn.hutool.core.lang.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.DefaultDialectTest;
import work.myfavs.framework.orm.meta.dialect.IDialect;

public class SqlServerDialectTest extends DefaultDialectTest {

  @Test
  public void getDialectName() {
    Assert.equals(DbType.SQL_SERVER, "sqlserver");
  }

  //  private static final String CASE1 = "SELECT * FROM TABLE_NAME";
  private static final String CASE1 = "SELECT * FROM TABLE_NAME";
  private static final String CASE2 = "SELECT * FROM TABLE_NAME ORDER BY col1";
  private static final String CASE3 =
      "WITH QUERY AS (SELECT * FROM TABLE_NAME) SELECT * FROM QUERY WHERE col1 = ? ORDER BY id";
  private static final String CASE4 =
      "SELECT * FROM (SELECT * FROM TABLE_NAME) QUERY WHERE col1 = ? ORDER BY id";

  @Test
  public void selectTop() {
    Sql selectTop1 = sqlServerDialect.selectTop(2, CASE1, null);
    Sql selectTop2 = sqlServerDialect.selectTop(3, CASE2, null);
    Sql selectTop3 = sqlServerDialect.selectTop(4, CASE3, null);
    Sql selectTop4 = sqlServerDialect.selectTop(4, CASE4, null);

    Assert.equals(selectTop1.toString(), "SELECT TOP 2 * FROM TABLE_NAME");
    Assert.equals(selectTop2.toString(), "SELECT TOP 3 * FROM TABLE_NAME ORDER BY col1");
    Assert.equals(
        selectTop3.toString(),
        "WITH QUERY AS ( SELECT * FROM TABLE_NAME ) SELECT TOP 4 * FROM QUERY WHERE col1 = ? ORDER BY id");
    Assert.equals(
        selectTop4.toString(),
        "SELECT TOP 4 * FROM ( SELECT * FROM TABLE_NAME ) QUERY WHERE col1 = ? ORDER BY id");
    perform(100, 10_000, () -> getDialect().selectTop(2, CASE1, null));
  }

  @Test
  public void selectPage() {
    Sql selectPage1 = getDialect().selectPage(true, CASE1, null, 2, 10);
    Sql selectPage2 = getDialect().selectPage(true, CASE2, null, 3, 10);
    Sql selectPage3 = getDialect().selectPage(true, CASE3, null, 4, 20);
    Sql selectPage4 = getDialect().selectPage(true, CASE4, null, 4, 20);

    Assert.equals(
        selectPage1.toString(),
        "SELECT * FROM ( SELECT *, ROW_NUMBER() OVER (ORDER BY CURRENT_TIMESTAMP) AS _rn FROM TABLE_NAME ) _paginate WHERE _rn BETWEEN 11 AND 20");
    Assert.equals(
        selectPage2.toString(),
        "SELECT * FROM ( SELECT *, ROW_NUMBER() OVER (ORDER BY col1) AS _rn FROM TABLE_NAME ) _paginate WHERE _rn BETWEEN 21 AND 30");
    Assert.equals(
        selectPage3.toString(),
        "WITH QUERY AS ( SELECT * FROM TABLE_NAME ) SELECT * FROM ( SELECT *, ROW_NUMBER() OVER (ORDER BY id) AS _rn FROM QUERY WHERE col1 = ? ) _paginate WHERE _rn BETWEEN 61 AND 80");
    Assert.equals(
        selectPage4.toString(),
        "SELECT * FROM ( SELECT *, ROW_NUMBER() OVER (ORDER BY id) AS _rn FROM ( SELECT * FROM TABLE_NAME ) QUERY WHERE col1 = ? ) _paginate WHERE _rn BETWEEN 61 AND 80");
    //    perform(100, 10_000, () -> getDialect().selectPage(2, 10, CASE1, null));
  }

  @Override
  protected IDialect getDialect() {
    return super.sqlServerDialect;
  }
}
