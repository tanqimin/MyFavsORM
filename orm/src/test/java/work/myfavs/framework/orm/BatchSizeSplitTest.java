package work.myfavs.framework.orm;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.*;
import org.junit.experimental.categories.Category;
import work.myfavs.framework.orm.entity.IdentityExample;
import work.myfavs.framework.orm.entity.SnowflakeExample;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.orm.Orm;
import work.myfavs.framework.orm.test.DatabaseConfigProvider;
import work.myfavs.framework.orm.test.IntegrationTest;
import work.myfavs.framework.orm.util.common.IOUtil;
import work.myfavs.framework.orm.util.common.StringUtil;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Category(IntegrationTest.class)
public class BatchSizeSplitTest {

  private static Database   database;
  private static DBTemplate dbTemplate;

  @BeforeClass
  public static void beforeClass() {

    DatabaseConfigProvider configProvider = DatabaseConfigProvider.create();
    String                 dbType         = configProvider.getDbType();
    String                 jdbcUrl        = configProvider.getJdbcUrl();
    String                 username       = configProvider.getUsername();
    String                 password       = configProvider.getPassword();

    HikariConfig configuration = new HikariConfig();
    configuration.setDriverClassName(configProvider.getDriverClassName());
    configuration.setJdbcUrl(jdbcUrl);
    configuration.setUsername(username);
    configuration.setPassword(password);
    configuration.setAutoCommit(false);
    DataSource dataSource = new HikariDataSource(configuration);

    dbTemplate =
        new DBTemplate.Builder()
            .dataSource(dataSource)
            .config(config -> config
                .setDbType(dbType)
                .setShowSql(true)
                .setShowResult(true)
                .setBatchSize(2))
            .build();

    database = dbTemplate.createDatabase();

    createTables(dbType);
  }

  @AfterClass
  public static void afterClass() {

    if (null != database) {
      database.close();
    }
    database   = null;
    dbTemplate = null;
  }

  private static void createTables(String dbType) {

    String sqlPath = getSqlPath(dbType);
    String sqlContent = IOUtil.read(sqlPath);
    List<String> sqlList = new ArrayList<>();
    if (DbType.SQL_SERVER.equals(dbType) || DbType.SQL_SERVER_2012.equals(dbType)) {
      for (String s : sqlContent.split("GO")) {
        sqlList.add(StringUtil.trim(s));
      }
    } else {
      for (String s : sqlContent.split(";")) {
        sqlList.add(StringUtil.trim(s));
      }
    }
    database.tx(em -> {
      for (String sql : sqlList) {
        if (StringUtil.isEmpty(sql)) continue;
        em.execute(new Sql(sql));
      }
    });
  }

  private static String getSqlPath(String dbType) {

    switch (dbType) {
      case DbType.MYSQL:
        return "sql/mysql/myfavs_master.sql";
      case DbType.SQL_SERVER:
      case DbType.SQL_SERVER_2012:
        return "sql/mssql/myfavs_master.sql";
      case DbType.POSTGRE_SQL:
        return "sql/postgresql/myfavs_master.sql";
      case DbType.H2:
      default:
        return "sql/h2/myfavs_master.sql";
    }
  }

  private static List<SnowflakeExample> createSnowflakeEntities() {

    List<SnowflakeExample> list = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      SnowflakeExample e = new SnowflakeExample();
      e.setCreated(new Date());
      e.setName("S" + i);
      e.setDisable(false);
      e.setPrice(new BigDecimal("100.00"));
      e.setType(TypeEnum.FOOD);
      list.add(e);
    }
    return list;
  }

  private static List<IdentityExample> createIdentityEntities() {

    List<IdentityExample> list = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      IdentityExample e = new IdentityExample();
      e.setCreated(new Date());
      e.setName("S" + i);
      e.setDisable(false);
      e.setPrice(new BigDecimal("100.00"));
      e.setType(TypeEnum.FOOD);
      list.add(e);
    }
    return list;
  }

  @Test
  public void shouldSplitBatchInsertForSnowflakeEntities() {

    try (Database db = dbTemplate.createDatabase()) {
      Orm orm = db.createOrm();
      orm.truncate(SnowflakeExample.class);

      List<SnowflakeExample> entities = createSnowflakeEntities();
      orm.create(SnowflakeExample.class, entities);

      long count = orm.count(new Sql("SELECT COUNT(*) FROM tb_snowflake"));
      Assert.assertEquals(5L, count);
    }
  }

  @Test
  public void shouldSplitBatchUpdateForSnowflakeEntities() {

    try (Database db = dbTemplate.createDatabase()) {
      Orm orm = db.createOrm();
      orm.truncate(SnowflakeExample.class);

      List<SnowflakeExample> entities = createSnowflakeEntities();
      orm.create(SnowflakeExample.class, entities);

      for (SnowflakeExample e : entities) {
        e.setPrice(new BigDecimal("200.00"));
      }
      orm.update(SnowflakeExample.class, entities);

      List<SnowflakeExample> updated =
          orm.find(SnowflakeExample.class, new Sql("SELECT * FROM tb_snowflake"));
      Assert.assertEquals(5, updated.size());
      for (SnowflakeExample e : updated) {
        Assert.assertEquals(0, e.getPrice().compareTo(new BigDecimal("200.00")));
      }
    }
  }

  @Test
  public void shouldSplitBatchInsertForIdentityEntities() {

    try (Database db = dbTemplate.createDatabase()) {
      Orm orm = db.createOrm();
      orm.truncate(IdentityExample.class);

      List<IdentityExample> entities = createIdentityEntities();
      orm.create(IdentityExample.class, entities);

      long count = orm.count(new Sql("SELECT COUNT(*) FROM tb_identity"));
      Assert.assertEquals(5L, count);

      List<Long> ids = orm.find(Long.class, new Sql("SELECT id FROM tb_identity"));
      Assert.assertEquals(5, ids.size());
      for (Long id : ids) {
        Assert.assertTrue(id > 0L);
      }
    }
  }
}
