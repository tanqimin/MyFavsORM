package work.myfavs.framework.orm;

import java.math.BigDecimal;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.entity.Snowflake;
import work.myfavs.framework.orm.entity.enums.TypeEnum;

public class DBTest extends AbstractTest {

  private Snowflake snowflake1;
  private Snowflake snowflake2;
  private Snowflake snowflake3;

  private List<Snowflake> snowflakes;

  @Before
  public void setUp() {
    snowflake1 = new Snowflake();
    snowflake1.setCreated(new Date());
    snowflake1.setName("S1");
    snowflake1.setPrice(new BigDecimal("199.00"));
    snowflake1.setType(TypeEnum.FOOD);

    snowflake2 = new Snowflake();
    snowflake2.setCreated(new Date());
    snowflake2.setName("S2");
    snowflake2.setPrice(new BigDecimal("299.00"));
    snowflake2.setType(TypeEnum.DRINK);

    snowflake3 = new Snowflake();
    snowflake3.setCreated(new Date());
    snowflake3.setName("S3");
    snowflake3.setPrice(new BigDecimal("399.00"));
    snowflake3.setType(TypeEnum.FOOD);

    snowflakes = new ArrayList<>();
    snowflakes.add(snowflake1);
    snowflakes.add(snowflake2);
    snowflakes.add(snowflake3);
  }

  @Test
  public void conn() {
    DB db1 = DB.conn(dbTemplate);
    Assert.assertNotNull(db1);

    DB db2 = DB.conn(DBConfig.DEFAULT_DATASOURCE_NAME);
    Assert.assertNotNull(db2);

    DB db3 = DB.conn();
    Assert.assertNotNull(db3);
  }

  @Test
  public void tx() {
    List<Snowflake> snowflakes =
        DB.conn(dbTemplate)
            .tx(
                db -> {
                  db.create(Snowflake.class, snowflake1);

                  Savepoint sp1 = db.setSavepoint("sp1");

                  try {
                    db.create(Snowflake.class, snowflake2);
                    throw new Exception("111");
                  } catch (Exception e) {
                    db.rollback(sp1);
                  }

                  db.create(Snowflake.class, snowflake3);
                  db.commit();

                  return db.find(Snowflake.class, "SELECT * FROM tb_snowflake", null);
                });
    Assert.assertEquals(2, snowflakes.size());
  }

  @Test
  public void testTx() {}

  @Test
  public void testTx1() {}

  @Test
  public void testTx2() {}

  @Test
  public void find() {
    List<Snowflake> snowflakes =
        DB.conn(dbTemplate).find(Snowflake.class, "SELECT * FROM tb_snowflake", null);
    Assert.assertNotNull(snowflakes);
    Assert.assertTrue(snowflakes.size() > 0);
  }

  @Test
  public void testFind() {}

  @Test
  public void testFind1() {}

  @Test
  public void testFind2() {}

  @Test
  public void testFind3() {}

  @Test
  public void findMap() {}

  @Test
  public void testFindMap() {}

  @Test
  public void findTop() {}

  @Test
  public void testFindTop() {}

  @Test
  public void testFindTop1() {}

  @Test
  public void testFindTop2() {}

  @Test
  public void get() {}

  @Test
  public void testGet() {}

  @Test
  public void testGet1() {}

  @Test
  public void testGet2() {}

  @Test
  public void getById() {}

  @Test
  public void getByField() {}

  @Test
  public void getByCond() {}

  @Test
  public void getByCondition() {}

  @Test
  public void testGetByCondition() {}

  @Test
  public void findByIds() {}

  @Test
  public void findByField() {}

  @Test
  public void testFindByField() {}

  @Test
  public void findByCond() {}

  @Test
  public void findByCondition() {}

  @Test
  public void testFindByCondition() {}

  @Test
  public void count() {}

  @Test
  public void testCount() {}

  @Test
  public void countByCond() {}

  @Test
  public void exists() {}

  @Test
  public void testExists() {}

  @Test
  public void testExists1() {}

  @Test
  public void existsByCond() {}

  @Test
  public void findPageLite() {}

  @Test
  public void testFindPageLite() {}

  @Test
  public void testFindPageLite1() {}

  @Test
  public void testFindPageLite2() {}

  @Test
  public void testFindPageLite3() {}

  @Test
  public void testFindPageLite4() {}

  @Test
  public void testFindPageLite5() {}

  @Test
  public void testFindPageLite6() {}

  @Test
  public void findPage() {}

  @Test
  public void testFindPage() {}

  @Test
  public void testFindPage1() {}

  @Test
  public void testFindPage2() {}

  @Test
  public void testFindPage3() {}

  @Test
  public void testFindPage4() {}

  @Test
  public void testFindPage5() {}

  @Test
  public void testFindPage6() {}

  @Test
  public void execute() {}

  @Test
  public void testExecute() {}

  @Test
  public void testExecute1() {}

  @Test
  public void testExecute2() {}

  @Test
  public void testExecute3() {}

  @Test
  public void testExecute4() {}

  @Test
  public void create() {}

  @Test
  public void snowFlakeId() {}

  @Test
  public void testCreate() {}

  @Test
  public void uuid() {}

  @Test
  public void update() {}

  @Test
  public void updateIgnoreNull() {}

  @Test
  public void testUpdate() {}

  @Test
  public void testUpdate1() {}

  @Test
  public void testUpdate2() {}

  @Test
  public void createOrUpdate() {}

  @Test
  public void delete() {}

  @Test
  public void testDelete() {}

  @Test
  public void deleteByIds() {}

  @Test
  public void deleteById() {}

  @Test
  public void deleteByCond() {}
}
