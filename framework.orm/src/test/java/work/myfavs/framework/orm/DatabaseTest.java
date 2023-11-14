package work.myfavs.framework.orm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.entity.*;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.entity.test.IIdentityTest;
import work.myfavs.framework.orm.entity.test.ILogicDeleteTest;
import work.myfavs.framework.orm.entity.test.ISnowflakeTest;
import work.myfavs.framework.orm.entity.test.IUuidTest;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.util.func.ThrowingFunction;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseTest extends AbstractTest
    implements ISnowflakeTest, IIdentityTest, IUuidTest, ILogicDeleteTest {

  private static final Logger log = LoggerFactory.getLogger(DatabaseTest.class);

  IPageable pageable =
      new IPageable() {
        @Override
        public boolean getEnablePage() {
          return true;
        }

        @Override
        public int getCurrentPage() {
          return 1;
        }

        @Override
        public int getPageSize() {
          return 2;
        }
      };

  @Before
  public void setUp() {}

  @Test
  public void tx() {

    initSnowflakes();
    database.truncate(Snowflake.class);

    ThrowingFunction<Database, List<Snowflake>, SQLException> func1 =
        db -> {
          db.create(Snowflake.class, SNOW_FLAKES.get(0));

          Savepoint sp1 = db.setSavepoint("sp1");

          try {
            db.create(Snowflake.class, SNOW_FLAKES.get(1));
            throw new Exception("111");
          } catch (Exception e) {
            db.rollback(sp1);
          }

          db.create(Snowflake.class, SNOW_FLAKES.get(2));
          db.commit();

          return db.find(Snowflake.class, new Sql("SELECT * FROM tb_snowflake"));
        };

    List<Snowflake> txSnowflakes = database.tx(func1);

    database
        .tx(
            db -> {
              db.delete(Snowflake.class, txSnowflakes);
              Savepoint savepoint = db.setSavepoint();
              log.debug("savepoint: {}", savepoint.getSavepointId());
              db.rollback();
            });
    Assert.assertEquals(2, txSnowflakes.size());
  }

  @Test
  public void testTx() {}

  @Test
  public void testTx1() {}

  @Test
  public void testTx2() {}

  @Test
  public void find() {
    initSnowflakes();
    database.truncate(Snowflake.class);
    database.create(Snowflake.class, SNOW_FLAKES);

    List<Snowflake> snowflakes = database.find(Snowflake.class, new Sql("SELECT * FROM tb_snowflake"));
    Assert.assertEquals(3, snowflakes.size());

    snowflakes =
        database.find(Snowflake.class, "SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1"));
    Assert.assertEquals(1, snowflakes.size());

    snowflakes = database.find(Snowflake.class, new Sql("SELECT * FROM tb_snowflake"));
    Assert.assertEquals(3, snowflakes.size());

    snowflakes = database.find(Snowflake.class, new Sql("SELECT id, type, name FROM tb_snowflake"));
    Assert.assertEquals(3, snowflakes.size());

    List<Record> records =
        database.findRecords(new Sql("SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1")));
    Assert.assertEquals(1, records.size());

    records = database.findRecords("SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1"));
    Assert.assertEquals(1, records.size());
  }

  @Test
  public void findMap() {
    initSnowflakes();
    database.truncate(Snowflake.class);
    database.create(Snowflake.class, SNOW_FLAKES);

    Map<Long, Snowflake> result =
        database
            .findMap(
                Snowflake.class,
                "id",
                "SELECT * FROM tb_snowflake WHERE disable = ?",
                List.of(false));

    result.forEach(
        (k, v) -> {
          Assert.assertTrue(k > 0L);
        });

    result =
        database
            .findMap(
                Snowflake.class,
                "id",
                new Sql("SELECT * FROM tb_snowflake WHERE disable = ?", List.of(false)));
    result.forEach(
        (k, v) -> {
          Assert.assertTrue(k > 0L);
        });
  }

  @Test
  public void findTop() {
    initSnowflakes();
    database.truncate(Snowflake.class);
    database.create(Snowflake.class, SNOW_FLAKES);

    Sql             sql       = Sql.SelectAll().from("tb_snowflake");
    List<Snowflake> snowflake = database.findTop(Snowflake.class, 1, sql);
    Assert.assertEquals(1, snowflake.size());

    snowflake = database.findTop(Snowflake.class, 1, sql.toString(), sql.getParams());
    Assert.assertEquals(1, snowflake.size());

    List<Record> records = database.findTopRecords(1, sql);

    Assert.assertEquals(1, records.size());

    records = database.findTopRecords(1, sql.toString(), sql.getParams());
    Assert.assertEquals(1, records.size());
  }

  @Test
  public void get() {
    initSnowflakes();
    database.truncate(Snowflake.class);
    database.create(Snowflake.class, SNOW_FLAKES);

    Sql       sql       = Sql.SelectAll().from("tb_snowflake");
    Snowflake snowflake = database.get(Snowflake.class, sql);
    Assert.assertNotNull(snowflake);
    snowflake = database.get(Snowflake.class, sql.toString(), sql.getParams());
    Assert.assertNotNull(snowflake);
    Record record = database.getRecord(sql);
    Assert.assertNotNull(record);
    record = database.getRecord(sql.toString(), sql.getParams());
    Assert.assertNotNull(record);
  }

  @Test
  public void getByXX() {
    initSnowflakes();
    database.truncate(Snowflake.class);
    database.create(Snowflake.class, SNOW_FLAKES);

    Sql       sql         = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("name", "S1"));
    Snowflake target      = database.get(Snowflake.class, sql);
    Long      snowflakeId = target.getId();

    Snowflake snowflake = database.getById(Snowflake.class, snowflakeId);
    Assert.assertNotNull(snowflake);

    snowflake = database.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertNotNull(snowflake);

    Snowflake condition = new Snowflake();
    condition.setName("S1");
    snowflake = database.getByCriteria(Snowflake.class, condition);
    Assert.assertNotNull(snowflake);

    snowflake = database.getByCriteria(Snowflake.class, condition, BaseEntity.Update.class);
    Assert.assertNotNull(snowflake);
  }

  @Test
  public void findByXX() {
    initUuids();
    database.truncate(Uuid.class);
    database.create(Uuid.class, UUIDS);

    List<String> uuids = UUIDS.stream().map(Uuid::getId).collect(Collectors.toList());

    List<Uuid> uuidList = database.findByIds(Uuid.class, uuids);
    Assert.assertEquals(uuidList.size(), 3);

    uuidList = database.findByField(Uuid.class, "name", "S1");
    Assert.assertEquals(uuidList.size(), 1);

    uuidList = database.findByField(Uuid.class, "id", uuids);
    Assert.assertEquals(uuidList.size(), 3);

    uuidList = database.findByCond(Uuid.class, Cond.eq("name", "S1"));
    Assert.assertEquals(uuidList.size(), 1);

    Uuid condition = new Uuid();
    condition.setName("S1");

    uuidList = database.findByCriteria(Uuid.class, condition);
    Assert.assertEquals(uuidList.size(), 1);

    uuidList = database.findByCriteria(Uuid.class, condition, BaseEntity.Update.class);
    Assert.assertEquals(uuidList.size(), 2);
  }

  @Test
  public void count() {
    initUuids();
    database.truncate(Uuid.class);
    database.create(Uuid.class, UUIDS);

    Sql  sql   = Sql.SelectAll().from("tb_uuid");
    long count = database.count(sql);
    Assert.assertEquals(3L, count);

    count = database.count(sql.toString(), sql.getParams());
    Assert.assertEquals(3L, count);

    count = database.countByCond(Uuid.class, Cond.eq("name", "S1"));
    Assert.assertEquals(1L, count);
  }

  @Test
  public void exists() {
    initIdentities();
    database.truncate(Identity.class);
    database.create(Identity.class, IDENTITIES);

    Sql     sql    = Sql.SelectAll().from("tb_identity").where(Cond.eq("name", "S1"));
    boolean exists = database.exists(sql);
    Assert.assertTrue(exists);

    exists = database.exists(sql.toString(), sql.getParams());
    Assert.assertTrue(exists);

    Identity identity = database.get(Identity.class, sql);
    exists = database.exists(Identity.class, identity);
    Assert.assertTrue(exists);

    exists = database.existsByCond(Identity.class, Cond.eq("name", "S1"));
    Assert.assertTrue(exists);
  }

  @Test
  public void findPageLite() {
    initSnowflakes();
    database.truncate(Snowflake.class);
    database.create(Snowflake.class, SNOW_FLAKES);

    Sql                 sql  = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("disable", false));
    PageLite<Snowflake> page = database.findPageLite(Snowflake.class, sql, true, 1, 2);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = database.findPageLite(Snowflake.class, sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = database.findPageLite(Snowflake.class, sql, pageable);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = database.findPageLite(Snowflake.class, sql.toString(), sql.getParams(), pageable);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    PageLite<Record> recordPage = database.findRecordsPageLite(sql, true, 1, 2);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = database.findRecordsPageLite(sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = database.findRecordsPageLite(sql, pageable);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = database.findRecordsPageLite(sql.toString(), sql.getParams(), pageable);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());
  }

  @Test
  public void findPage() {
    initSnowflakes();
    initUuids();
    database.truncate(Snowflake.class);
    database.truncate(Uuid.class);
    database.create(Snowflake.class, SNOW_FLAKES);
    database.create(Uuid.class, UUIDS);

    Sql             sql  = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("disable", false));
    Sql             sql2 = Sql.Select("id").from("tb_uuid").where(Cond.eq("disable", false));
    Page<Snowflake> page = database.findPage(Snowflake.class, sql, true, 1, 2);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = database.findPage(Snowflake.class, sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = database.findPage(Snowflake.class, sql, pageable);
    Sql sqlWithId = Sql.Select("id").from("tb_snowflake").where(Cond.eq("disable", false));
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = database.findPage(Snowflake.class, sql.toString(), sql.getParams(), pageable);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    Page<String> strPage = database.findPage(String.class, sql2, true, 1, 2);
    Assert.assertEquals(2, strPage.getData().size());
    Assert.assertEquals(2L, strPage.getTotalPages());
    Assert.assertEquals(3L, strPage.getTotalRecords());

    Page<Record> recordPage = database.findRecordsPage(sql, true, 1, 2);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = database.findRecordsPage(sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = database.findRecordsPage(sql, pageable);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = database.findRecordsPage(sql.toString(), sql.getParams(), pageable);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());
  }

  @Test
  public void execute() {
    initSnowflakes();
    database.truncate(Snowflake.class);
    database.create(Snowflake.class, SNOW_FLAKES);

    String sql = "update tb_snowflake set disable = ? where name = ?";
    database.execute(new Sql(sql, List.of(true, "S1")), 3000);
    Snowflake snowflake = database.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertTrue(snowflake.getDisable());

    database.execute(sql, List.of(false, "S1"));
    snowflake = database.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertFalse(snowflake.getDisable());

    database.execute(new Sql(sql, List.of(true, "S1")), 3000);
    snowflake = database.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertTrue(snowflake.getDisable());

    database.execute(sql, List.of(false, "S1"), 3000);
    snowflake = database.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertFalse(snowflake.getDisable());

    List<Sql> sqlList = new ArrayList<>();
    sqlList.add(new Sql(sql, List.of(true, "S1")));
    sqlList.add(new Sql(sql, List.of(true, "S2")));
    sqlList.add(new Sql(sql, List.of(true, "S3")));

    database.execute(sqlList);
    List<Snowflake> snowflakes =
        database.findByField(Snowflake.class, "name", List.of("S1", "S2", "S3"));
    snowflakes.forEach(s -> Assert.assertTrue(s.getDisable()));

    sqlList.clear();
    sqlList.add(new Sql(sql, List.of(false, "S1")));
    sqlList.add(new Sql(sql, List.of(false, "S2")));
    sqlList.add(new Sql(sql, List.of(false, "S3")));

    database.execute(sqlList);
    snowflakes = database.findByField(Snowflake.class, "name", List.of("S1", "S2", "S3"));
    snowflakes.forEach(s -> Assert.assertFalse(s.getDisable()));
  }

  @Test
  public void create() {
    initSnowflakes();
    initUuids();
    initIdentities();

    database.truncate(Snowflake.class);
    database.truncate(Uuid.class);
    database.truncate(Identity.class);

    Snowflake snowflake = new Snowflake();
    snowflake.setCreated(new Date());
    snowflake.setName("S1");
    snowflake.setPrice(new BigDecimal("199.00"));
    snowflake.setType(TypeEnum.FOOD);

    Uuid uuid = new Uuid();
    uuid.setCreated(new Date());
    uuid.setName("S1");
    uuid.setPrice(new BigDecimal("199.00"));
    uuid.setType(TypeEnum.FOOD);

    Identity identity = new Identity();
    identity.setCreated(new Date());
    identity.setName("S1");
    identity.setPrice(new BigDecimal("199.00"));
    identity.setType(TypeEnum.FOOD);

    database.create(Snowflake.class, snowflake);
    database.create(Uuid.class, uuid);
    database.create(Identity.class, identity);

    Assert.assertTrue(snowflake.getId() > 0L);
    Assert.assertNotNull(uuid.getId());
    Assert.assertTrue(identity.getId() > 0L);

    Assert.assertNotNull(database.getById(Snowflake.class, snowflake.getId()));
    Assert.assertNotNull(database.getById(Uuid.class, uuid.getId()));
    Assert.assertNotNull(database.getById(Identity.class, identity.getId()));
  }

  @Test
  public void testId() {
    long   snowFlakeId = dbTemplate.getPkGenerator().nextSnowFakeId();
    String uuid        = dbTemplate.getPkGenerator().nextUUID();
    Assert.assertTrue(snowFlakeId > 0);
    Assert.assertNotNull(uuid);
  }

  @Test
  public void testCreate() {
    initSnowflakes();
    initUuids();
    initIdentities();

    database.truncate(Snowflake.class);
    database.truncate(Uuid.class);
    database.truncate(Identity.class);

    database.create(Snowflake.class, SNOW_FLAKES);
    database.create(Uuid.class, UUIDS);
    database.create(Identity.class, IDENTITIES);

    SNOW_FLAKES.forEach(
        s -> {
          Assert.assertNotNull(s.getId());
          Assert.assertTrue(s.getId() > 0L);
        });

    UUIDS.forEach(
        s -> {
          Assert.assertNotNull(s.getId());
          Assert.assertTrue(s.getId().length() > 0);
        });

    IDENTITIES.forEach(
        s -> {
          Assert.assertNotNull(s.getId());
          Assert.assertTrue(s.getId() > 0L);
        });
  }

  @Test
  public void update() {
    initSnowflakes();
    initUuids();
    initIdentities();

    database.truncate(Snowflake.class);
    database.truncate(Uuid.class);
    database.truncate(Identity.class);

    database.create(Snowflake.class, SNOW_FLAKES);
    database.create(Uuid.class, UUIDS);
    database.create(Identity.class, IDENTITIES);

    Snowflake snowflake = database.getByField(Snowflake.class, "name", "S1");
    Uuid      uuid      = database.getByField(Uuid.class, "name", "S1");
    Identity  identity  = database.getByField(Identity.class, "name", "S1");

    database
        .tx(
            db -> {
              snowflake.setPrice(new BigDecimal("999.00"));
              uuid.setPrice(new BigDecimal("999.00"));
              identity.setPrice(new BigDecimal("999.00"));

              db.update(Snowflake.class, snowflake);
              db.update(Uuid.class, uuid);
              db.update(Identity.class, identity);

              Snowflake dbSnowflake = db.getById(Snowflake.class, snowflake.getId());
              Uuid      dbUuid      = db.getById(Uuid.class, uuid.getId());
              Identity  dbIdentity  = db.getById(Identity.class, identity.getId());

              Assert.assertEquals(dbSnowflake.getPrice().compareTo(new BigDecimal("999.00")), 0);
              Assert.assertEquals(dbUuid.getPrice().compareTo(new BigDecimal("999.00")), 0);
              Assert.assertEquals(dbIdentity.getPrice().compareTo(new BigDecimal("999.00")), 0);

              Snowflake condSnowflake = new Snowflake();
              condSnowflake.setId(dbTemplate.getPkGenerator().nextSnowFakeId());
              condSnowflake.setPrice(new BigDecimal("199.00"));
              db.updateIgnoreNull(Snowflake.class, condSnowflake);

              dbSnowflake = db.getById(Snowflake.class, snowflake.getId());

              Assert.assertEquals(snowflake.getName(), dbSnowflake.getName());
              Assert.assertEquals(snowflake.getType(), dbSnowflake.getType());
              Assert.assertEquals(snowflake.getDisable(), dbSnowflake.getDisable());
              Assert.assertEquals(snowflake.getCreated(), dbSnowflake.getCreated());
              Assert.assertEquals(snowflake.getPrice().compareTo(dbSnowflake.getPrice()), 0);

              dbUuid.setPrice(new BigDecimal("199.00"));
              db.update(Uuid.class, dbUuid, new String[]{"price"});

              dbUuid = db.getById(Uuid.class, uuid.getId());
              Assert.assertEquals(dbUuid.getPrice().compareTo(new BigDecimal("199.00")), 0);

              for (Identity i : IDENTITIES) {
                i.setPrice(new BigDecimal("1099.00"));
              }
              db.update(Identity.class, IDENTITIES, new String[]{"price"});

              List<Identity> identities = db.find(Identity.class, new Sql("select * from tb_identity"));

              for (Identity i : identities) {
                Assert.assertEquals(i.getPrice().compareTo(new BigDecimal("1099.00")), 0);
              }

              for (Uuid u : UUIDS) {
                u.setType(TypeEnum.DRINK);
              }

              db.update(Uuid.class, UUIDS);

              List<Uuid> uuids = db.find(Uuid.class, new Sql("select * from tb_uuid"));
              for (Uuid u : uuids) {
                Assert.assertEquals(u.getType(), TypeEnum.DRINK);
              }
            });
  }

  @Test
  public void createOrUpdate() {
    initSnowflakes();
    initUuids();
    initIdentities();

    database.truncate(Snowflake.class);
    database.truncate(Uuid.class);
    database.truncate(Identity.class);

    Snowflake snowflake = SNOW_FLAKES.get(0);
    Uuid      uuid      = UUIDS.get(0);
    Identity  identity  = IDENTITIES.get(0);

    Snowflake dbSnowflake = database.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Uuid      dbUuid      = database.getByCond(Uuid.class, Cond.eq("name", "S1"));
    Identity  dbIdentity  = database.getByCond(Identity.class, Cond.eq("name", "S1"));

    Assert.assertNull(dbSnowflake);
    Assert.assertNull(dbUuid);
    Assert.assertNull(dbIdentity);

    database.createOrUpdate(Snowflake.class, snowflake);
    database.createOrUpdate(Uuid.class, uuid);
    database.createOrUpdate(Identity.class, identity);

    dbSnowflake = database.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    dbUuid = database.getByCond(Uuid.class, Cond.eq("name", "S1"));
    dbIdentity = database.getByCond(Identity.class, Cond.eq("name", "S1"));

    Assert.assertNotNull(dbSnowflake);
    Assert.assertNotNull(dbUuid);
    Assert.assertNotNull(dbIdentity);

    snowflake.setPrice(new BigDecimal("999.99"));
    uuid.setPrice(new BigDecimal("999.99"));
    identity.setPrice(new BigDecimal("999.99"));

    database.createOrUpdate(Snowflake.class, snowflake);
    database.createOrUpdate(Uuid.class, uuid);
    database.createOrUpdate(Identity.class, identity);

    dbSnowflake = database.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    dbUuid = database.getByCond(Uuid.class, Cond.eq("name", "S1"));
    dbIdentity = database.getByCond(Identity.class, Cond.eq("name", "S1"));

    Assert.assertEquals(dbSnowflake.getPrice().compareTo(new BigDecimal("999.99")), 0);
    Assert.assertEquals(dbUuid.getPrice().compareTo(new BigDecimal("999.99")), 0);
    Assert.assertEquals(dbIdentity.getPrice().compareTo(new BigDecimal("999.99")), 0);
  }

  @Test
  public void delete() {
    initSnowflakes();
    initUuids();
    initIdentities();
    initLogicDeletes();

    database.truncate(Snowflake.class);
    database.truncate(Uuid.class);
    database.truncate(Identity.class);
    database.truncate(LogicDelete.class);

    database.create(Snowflake.class, SNOW_FLAKES);
    database.create(Uuid.class, UUIDS);
    database.create(Identity.class, IDENTITIES);
    database.create(LogicDelete.class, LOGIC_DELETES);

    database.delete(Snowflake.class, SNOW_FLAKES.get(0));
    database.deleteById(Snowflake.class, SNOW_FLAKES.get(1).getId());
    database.deleteByCond(Snowflake.class, Cond.eq("name", "S3"));

    database.delete(Uuid.class, UUIDS);

    List<Long> ids = IDENTITIES.stream().map(Identity::getId).collect(Collectors.toList());
    database.deleteByIds(Identity.class, ids);

    database.deleteByCond(LogicDelete.class, Cond.eq("name", "S1"));

    long c1 = database.count(new Sql("SELECT * FROM tb_snowflake"));
    long c2 = database.count(new Sql("SELECT * FROM tb_uuid"));
    long c3 = database.count(new Sql("SELECT * FROM tb_identity"));
    long c4 = database.count(new Sql("SELECT * FROM tb_logic_delete"));

    Assert.assertEquals(c1, 0);
    Assert.assertEquals(c2, 0);
    Assert.assertEquals(c3, 0);
    Assert.assertEquals(c4, 3);
  }
}
