package work.myfavs.framework.orm.meta.dialect.impl;

import cn.hutool.core.lang.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.DefaultDialectTest;
import work.myfavs.framework.orm.meta.dialect.IDialect;

public class MySqlDialectTest extends DefaultDialectTest {

  @Test
  public void getDialectName() {
    Assert.equals(DbType.MYSQL, "mysql");
  }

  private static final String CASE1 = "SELECT * FROM TABLE_NAME";
  private static final String CASE2 = "SELECT * FROM TABLE_NAME ORDER BY col1";
  private static final String CASE3 =
      "WITH QUERY AS ( SELECT * FROM TABLE_NAME ) SELECT * FROM QUERY WHERE col1 = ?";

  @Test
  public void selectTop() {
    Sql selectTop1 = getDialect().selectTop(2, CASE1, null);
    Sql selectTop2 = getDialect().selectTop(3, CASE2, null);
    Sql selectTop3 = getDialect().selectTop(4, CASE3, null);

    Assert.equals(selectTop1.toString(), CASE1 + " LIMIT 2");
    Assert.equals(selectTop2.toString(), CASE2 + " LIMIT 3");
    Assert.equals(selectTop3.toString(), CASE3 + " LIMIT 4");
    //    perform(100, 10_000, () -> getDialect().selectTop(2, CASE3, null));
  }

  @Test
  public void selectPage() {
    Sql selectPage1 = getDialect().selectPage(2, 10, CASE1, null);
    Sql selectPage2 = getDialect().selectPage(3, 10, CASE2, null);
    Sql selectPage3 = getDialect().selectPage(4, 20, CASE3, null);

    Assert.equals(selectPage1.toString(), CASE1 + " LIMIT 10, 10");
    Assert.equals(selectPage2.toString(), CASE2 + " LIMIT 20, 10");
    Assert.equals(selectPage3.toString(), CASE3 + " LIMIT 60, 20");
    //    perform(100, 10_000, () -> getDialect().selectPage(2, 10, CASE1, null));
  }

  @Override
  protected IDialect getDialect() {
    return super.mySqlDialect;
  }
}
