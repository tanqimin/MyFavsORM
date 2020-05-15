package work.myfavs.framework.orm;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.DBTemplate.Builder;
import work.myfavs.framework.orm.entity.Snowfake;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.meta.clause.Sql;

public class DBTest {

  String url = "jdbc:mysql://127.0.0.1:3306/myfavs_test?useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
  String user = "root";
  String password = "root";

  private DBTemplate dbTemplate;

  @Before
  public void setUp()
      throws Exception {

    HikariConfig configuration = new HikariConfig();
    configuration.setDriverClassName("com.mysql.jdbc.Driver");
    configuration.setJdbcUrl(url);
    configuration.setUsername(user);
    configuration.setPassword(password);
    configuration.setAutoCommit(false);
    DataSource dataSource = new HikariDataSource(configuration);

    this.dbTemplate = new Builder().dataSource(dataSource)
        .config(config -> {
          config.setShowSql(true)
              .setShowResult(true);
        })
        .build();
  }

  @Test
  public void find() {

    List<Snowfake> snowfakes = DB.conn().find(Snowfake.class, "SELECT * FROM tb_snowflake", null);
    Assert.assertNotNull(snowfakes);
    Assert.assertTrue(snowfakes.size() > 0);
  }

  @Test
  public void getByCondition() {

    Snowfake snowfake = new Snowfake();
    snowfake.setName("UUI%");
    snowfake.setType(TypeEnum.DRINK);

    DB.conn().tx(db -> {
      Snowfake res = db.getByCondition(Snowfake.class, snowfake);
      Assert.assertNotNull(res);

      List<Snowfake> ress = db.findByCondition(Snowfake.class, snowfake, "SNOW_DTO");
      Assert.assertNotNull(ress);
    });
  }

  @Test
  public void testTransaction() {
    Snowfake snowfake = new Snowfake();
    snowfake.setCreated(new Date());
    snowfake.setName("snowfake");
    snowfake.setDisable(false);
    snowfake.setPrice(new BigDecimal(100));
    snowfake.setType(TypeEnum.DRINK);

    DB.conn().tx(db -> {
      long count = getCount(db);
      db.create(Snowfake.class, snowfake);
      Assert.assertEquals(++count, getCount(db));
    });
  }

  private long getCount(DB db) {

    return db.count(new Sql("SELECT * FROM tb_snowfake"));
  }

  @Test
  public void commit() {

  }

  @Test
  public void rollback() {

  }

  @Test
  public void close() {

  }

  @Test
  public void findTop() {

  }

  @Test
  public void testFindTop() {

  }

  @Test
  public void testFindTop1() {

  }

  @Test
  public void testFindTop2() {

  }

  @Test
  public void get() {

  }

  @Test
  public void testGet() {

  }

  @Test
  public void testGet1() {

  }

  @Test
  public void testGet2() {

  }

  @Test
  public void getById() {

  }

  @Test
  public void getByField() {

  }

  @Test
  public void findByIds() {

  }

  @Test
  public void findByField() {

  }

  @Test
  public void testFindByField() {

  }

  @Test
  public void count() {

  }

  @Test
  public void testCount() {

  }

  @Test
  public void findPageLite() {

  }

  @Test
  public void testFindPageLite() {

  }

  @Test
  public void testFindPageLite1() {

  }

  @Test
  public void testFindPageLite2() {

  }

  @Test
  public void findPage() {

  }

  @Test
  public void testFindPage() {

  }

  @Test
  public void testFindPage1() {

  }

  @Test
  public void testFindPage2() {

  }

  @Test
  public void execute() {

  }

  @Test
  public void testExecute() {

  }

  @Test
  public void testExecute1() {

  }

  @Test
  public void create() {

    Snowfake snowfake = new Snowfake();
    snowfake.setCreated(new Date());
    snowfake.setName("create test");
    snowfake.setDisable(false);
    snowfake.setPrice(new BigDecimal(199));
    snowfake.setType(TypeEnum.DRINK);
    snowfake.setConfig("");

    DB.conn().create(Snowfake.class, snowfake);
  }

  @Test
  public void testCreate() {

  }

  @Test
  public void update() {

  }

  @Test
  public void testUpdate() {

  }

  @Test
  public void testUpdate1() {

  }

  @Test
  public void testUpdate2() {

  }

  @Test
  public void delete() {

  }

  @Test
  public void testDelete() {

  }

  @Test
  public void deleteByIds() {

  }

  @Test
  public void deleteById() {

  }

}