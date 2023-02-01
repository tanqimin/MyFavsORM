package work.myfavs.framework.orm.meta.dialect.impl;

import cn.hutool.core.lang.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.DefaultDialectTest;
import work.myfavs.framework.orm.meta.dialect.IDialect;

public class OracleDialectTest extends DefaultDialectTest {

  @Test
  public void getDialectName() {
    Assert.equals(DbType.ORACLE, "oracle");
  }

  private static final String CASE1 = "SELECT * FROM TABLE_NAME";
  private static final String CASE2 = "SELECT * FROM TABLE_NAME ORDER BY col1";
  @Test
  public void selectTop() {
    Sql selectTop1 = getDialect().selectTop(2, CASE1, null);
    Sql selectTop2 = getDialect().selectTop(3, CASE2, null);

    Assert.equals(selectTop1.toString(), "SELECT _limit.*, ROWNUM AS _rn FROM ( SELECT * FROM TABLE_NAME ) _limit WHERE _limit.ROWNUM <= 2");
    Assert.equals(selectTop2.toString(), "SELECT _limit.*, ROWNUM AS _rn FROM ( SELECT * FROM TABLE_NAME ORDER BY col1 ) _limit WHERE _limit.ROWNUM <= 3");
    //    perform(100, 10_000, () -> getDialect().selectTop(2, CASE3, null));
  }

  @Test
  public void selectPage() {
    Sql selectPage1 = getDialect().selectPage(2, 10, CASE1, null);
    Sql selectPage2 = getDialect().selectPage(3, 10, CASE2, null);

    Assert.equals(selectPage1.toString(), "SELECT _paginate.* FROM ( SELECT _limit.*, ROWNUM AS _rn FROM ( SELECT * FROM TABLE_NAME ) _limit WHERE _limit.ROWNUM <= 20 ) _paginate WHERE _paginate._rn > 10");
    Assert.equals(selectPage2.toString(), "SELECT _paginate.* FROM ( SELECT _limit.*, ROWNUM AS _rn FROM ( SELECT * FROM TABLE_NAME ORDER BY col1 ) _limit WHERE _limit.ROWNUM <= 30 ) _paginate WHERE _paginate._rn > 20");
    //    perform(100, 10_000, () -> getDialect().selectPage(2, 10, CASE1, null));
  }

  @Override
  protected IDialect getDialect() {
    return super.oracleDialect;
  }
}
