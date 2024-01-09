package work.myfavs.framework.orm;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.entity.IdentityExample;
import work.myfavs.framework.orm.entity.SnowflakeExample;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.entity.test.IIdentityTest;
import work.myfavs.framework.orm.entity.test.ILogicDeleteTest;
import work.myfavs.framework.orm.entity.test.ISnowflakeTest;
import work.myfavs.framework.orm.entity.test.IUuidTest;
import work.myfavs.framework.orm.orm.Orm;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryTest extends AbstractTest
    implements ISnowflakeTest, IIdentityTest, IUuidTest, ILogicDeleteTest {

  private static final Logger log = LoggerFactory.getLogger(QueryTest.class);

  @Test
  public void createQuery() {
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    String sql = "INSERT INTO tb_snowflake (id, created, name, disable, price , type, config) VALUES (?, ?, ?, ?, ? , ?, ?)";
    try (Query query = database.createQuery(sql)) {
      query.addParameter(1, dbTemplate.getPkGenerator().nextSnowFakeId());
      query.addParameter(2, new Date());
      query.addParameter(3, "tb_identity");
      query.addParameter(4, false);
      query.addParameter(5, new BigDecimal("199.00"));
      query.addParameter(6, TypeEnum.FOOD);
      query.addParameter(7, null);
      assertEquals(query.execute(), 1);
      List<SnowflakeExample> snowflakes = query.createQuery("SELECT * FROM tb_snowflake").find(SnowflakeExample.class);
      assertEquals(snowflakes.size(), 1);

    }
  }

  @Test
  public void testCreateQuery() {
    Orm orm = database.createOrm();
    orm.truncate(IdentityExample.class);

    String sql = "INSERT INTO tb_identity (created, name, disable, price , type, config) VALUES (?, ?, ?, ? , ?, ?)";
    try (Query query = database.createQuery(sql, true)) {
      query.addParameter(1, new Date());
      query.addParameter(2, "tb_identity");
      query.addParameter(3, false);
      query.addParameter(4, new BigDecimal("199.00"));
      query.addParameter(5, TypeEnum.FOOD);
      query.addParameter(6, null);

      query.execute(null, rs -> {
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.getLong(1) > 0L);
      });
    }
  }

  @Test
  public void addParameters() {
  }

  @Test
  public void addParameter() {
  }

  @Test
  public void testAddParameter() {
  }

  @Test
  public void find() {
  }

  @Test
  public void get() {
  }

  @Test
  public void execute() {
  }

  @Test
  public void testExecute() {
  }

  @Test
  public void addBatch() {
  }

  @Test
  public void executeBatch() {
  }

  @Test
  public void testExecuteBatch() {
  }

  @Test
  public void close() {
  }
}