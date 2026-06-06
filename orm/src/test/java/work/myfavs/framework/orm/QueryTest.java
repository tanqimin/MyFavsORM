package work.myfavs.framework.orm;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import work.myfavs.framework.orm.entity.IdentityExample;
import work.myfavs.framework.orm.entity.SnowflakeExample;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.entity.test.*;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.orm.Orm;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QueryTest extends AbstractTest
    implements ISnowflakeTest, IIdentityTest, IUuidTest, ILogicDeleteTest, IAssignedTest {

  String SQL_INSERT_SNOW_FLAKE = "INSERT INTO tb_snowflake (id, created, name, disable, price , type, config) VALUES (?, ?, ?, ?, ? , ?, ?)";
  String SQL_INSERT_IDENTITY   = "INSERT INTO tb_identity (created, name, disable, price , type, config) VALUES (?, ?, ?, ? , ?, ?)";
  String SQL_INSERT_ASSIGNED   = "INSERT INTO tb_assigned (epc) VALUES (?)";

  @Test
  public void createQuery() {

    try (Query queryMock = Mockito.mock(Query.class)) {
      queryMock.createQuery(SQL_INSERT_SNOW_FLAKE);
      Mockito.verify(queryMock).createQuery(SQL_INSERT_SNOW_FLAKE);
    }

    try (Query queryMock = Mockito.mock(Query.class)) {
      queryMock.createQuery(SQL_INSERT_IDENTITY, true);
      Mockito.verify(queryMock).createQuery(SQL_INSERT_IDENTITY, true);
    }

    try (Query queryMock = Mockito.mock(Query.class)) {
      queryMock.createQuery(SQL_INSERT_ASSIGNED, true);
      Mockito.verify(queryMock).createQuery(SQL_INSERT_ASSIGNED, true);
    }
  }

  @Test
  public void find() {
    execute();

    String sql = "SELECT * FROM tb_identity WHERE name IN (?, ?, ?)";
    try (Query query = database.createQuery(sql)) {
      query.addParameters(List.of("tb_identity", "tb_identity1", "tb_identity2"));
      List<IdentityExample> identityExamples = query.find(IdentityExample.class);
      assertEquals(identityExamples.size(), 1);

      List<Record> records = query.find(Record.class);
      assertEquals(records.size(), 1);
    }
  }

  @Test
  public void get() {
    execute();

    String sql = "SELECT * FROM tb_identity WHERE name = ?";
    try (Query query = database.createQuery(sql)) {
      String param = "tb_identity";
      query.addParameter(1, param);
      IdentityExample identityExample = query.get(IdentityExample.class);
      assertEquals(identityExample.getName(), param);

      Record record = query.get(Record.class);
      assertEquals(record.get("name"), param);
    }
  }

  @Test
  public void execute() {
    Orm orm = database.createOrm();
    orm.truncate(IdentityExample.class);


    try (Query query = database.createQuery(SQL_INSERT_IDENTITY, true)) {
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
  public void testExecute() {
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);


    try (Query query = database.createQuery(SQL_INSERT_SNOW_FLAKE)) {
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