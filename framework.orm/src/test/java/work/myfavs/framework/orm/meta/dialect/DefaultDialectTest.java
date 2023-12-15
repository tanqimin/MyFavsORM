package work.myfavs.framework.orm.meta.dialect;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;
import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.entity.Identity;
import work.myfavs.framework.orm.entity.Snowflake;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.dialect.impl.MySqlDialect;
import work.myfavs.framework.orm.meta.dialect.impl.OracleDialect;
import work.myfavs.framework.orm.meta.dialect.impl.SqlServer2012Dialect;
import work.myfavs.framework.orm.meta.dialect.impl.SqlServerDialect;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;

public class DefaultDialectTest {
  protected DBConfig dbConfig             = new DBConfig();
  protected IDialect mySqlDialect         = new MySqlDialect(dbConfig);
  protected IDialect sqlServerDialect     = new SqlServerDialect(dbConfig);
  protected IDialect sqlServer2012Dialect = new SqlServer2012Dialect(dbConfig);
  protected IDialect oracleDialect        = new OracleDialect(dbConfig);

  private static final String CASE1 = "SELECT * FROM TABLE_NAME WHERE col1 = ? ORDER BY id desc";
  private static final String CASE2 =
      "WITH QUERY AS (SELECT * FROM TABLE_NAME) SELECT * FROM QUERY WHERE col1 = ? ORDER BY id desc";

  @Test
  public void count() {

    Sql count1 = mySqlDialect.count(CASE1, null);
    Sql count2 = mySqlDialect.count(CASE2, null);
    Sql count3 = mySqlDialect.count(Snowflake.class);

    Assert.equals(count1.toString(), "SELECT COUNT(*)\n" + "FROM TABLE_NAME\n" + "WHERE col1 = ?");
    Assert.equals(
        count2.toString(),
        "WITH QUERY AS (\n"
            + "\t\tSELECT *\n"
            + "\t\tFROM TABLE_NAME\n"
            + "\t)\n"
            + "SELECT COUNT(*)\n"
            + "FROM QUERY\n"
            + "WHERE col1 = ?");
    Assert.equals(count3.toString(), "SELECT COUNT(*) FROM tb_snowflake");

    perform(100, 10_000, () -> mySqlDialect.count(CASE2, null));
  }

  protected static void perform(int batch, int loop, Runnable runnable) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    for (int i = 0; i < batch; i++) {
      for (int j = 0; j < loop; j++) {
        runnable.run();
      }
    }
    stopWatch.stop();
    System.out.println(
        StrUtil.format(
            "exec {} times from {} loop use {} ms",
            batch,
            loop,
            stopWatch.getLastTaskTimeMillis()));
  }

  @Test
  public void select() {
    Sql select = mySqlDialect.select(Snowflake.class);
    Assert.equals(select.toString(), "SELECT * FROM tb_snowflake");
  }

  protected IDialect getDialect() {
    return null;
  }

  @Test
  public void insert() {
    String insert1 = mySqlDialect.insert(Metadata.entityMeta(Identity.class));
    String insert2 = mySqlDialect.insert(Metadata.entityMeta(Snowflake.class));

    Assert.notNull(insert1);
    Assert.notNull(insert2);
  }
}
