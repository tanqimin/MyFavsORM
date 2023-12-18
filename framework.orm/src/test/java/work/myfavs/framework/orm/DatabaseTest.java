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
    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);

    ThrowingFunction<Orm, List<Snowflake>, SQLException> func1 =
        innerOrm -> {
          innerOrm.create(Snowflake.class, SNOW_FLAKES.get(0));

          Savepoint sp1 = database.setSavepoint("sp1");

          try {
            innerOrm.create(Snowflake.class, SNOW_FLAKES.get(1));
            throw new Exception("111");
          } catch (Exception e) {
            database.rollback(sp1);
          }

          innerOrm.create(Snowflake.class, SNOW_FLAKES.get(2));

          return innerOrm.find(Snowflake.class, new Sql("SELECT * FROM tb_snowflake"));
        };

    List<Snowflake> txSnowflakes = database.tx(func1);

    database
        .tx(
            innerOrm -> {
              innerOrm.delete(Snowflake.class, txSnowflakes);
              Savepoint savepoint = database.setSavepoint();
              log.debug("savepoint: {}", savepoint.getSavepointId());
              database.rollback();
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

    database.tx(orm -> {
      orm.truncate(Snowflake.class);
      orm.create(Snowflake.class, SNOW_FLAKES);

      List<Snowflake> snowflakes = orm.find(Snowflake.class, new Sql("SELECT * FROM tb_snowflake"));
      Assert.assertEquals(3, snowflakes.size());

      snowflakes = orm.find(Snowflake.class, "SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1"));
      Assert.assertEquals(1, snowflakes.size());

      snowflakes = orm.find(Snowflake.class, new Sql("SELECT * FROM tb_snowflake"));
      Assert.assertEquals(3, snowflakes.size());

      snowflakes = orm.find(Snowflake.class, new Sql("SELECT id, type, name FROM tb_snowflake"));
      Assert.assertEquals(3, snowflakes.size());

      List<Record> records = orm.findRecords(new Sql("SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1")));
      Assert.assertEquals(1, records.size());

      records = orm.findRecords("SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1"));
      Assert.assertEquals(1, records.size());
    });
  }

  @Test
  public void findMap() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.create(Snowflake.class, SNOW_FLAKES);

    Map<Long, Snowflake> result = orm.findMap(
        Snowflake.class,
        "id",
        "SELECT * FROM tb_snowflake WHERE disable = ?",
        List.of(false));

    result.forEach((k, v) -> Assert.assertTrue(k > 0L));

    result = orm.findMap(
        Snowflake.class,
        "id",
        new Sql("SELECT * FROM tb_snowflake WHERE disable = ?", List.of(false)));
    result.forEach((k, v) -> Assert.assertTrue(k > 0L));
  }

  @Test
  public void findTop() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.create(Snowflake.class, SNOW_FLAKES);

    Sql             sql       = Sql.SelectAll().from("tb_snowflake");
    List<Snowflake> snowflake = orm.findTop(Snowflake.class, 1, sql);
    Assert.assertEquals(1, snowflake.size());

    snowflake = orm.findTop(Snowflake.class, 1, sql.toString(), sql.getParams());
    Assert.assertEquals(1, snowflake.size());

    List<Record> records = orm.findTopRecords(1, sql);

    Assert.assertEquals(1, records.size());

    records = orm.findTopRecords(1, sql.toString(), sql.getParams());
    Assert.assertEquals(1, records.size());
  }

  @Test
  public void get() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.create(Snowflake.class, SNOW_FLAKES);

    Sql       sql       = Sql.SelectAll().from("tb_snowflake");
    Snowflake snowflake = orm.get(Snowflake.class, sql);
    Assert.assertNotNull(snowflake);
    snowflake = orm.get(Snowflake.class, sql.toString(), sql.getParams());
    Assert.assertNotNull(snowflake);
    Record record = orm.getRecord(sql);
    Assert.assertNotNull(record);
    record = orm.getRecord(sql.toString(), sql.getParams());
    Assert.assertNotNull(record);
  }

  @Test
  public void getByXX() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.create(Snowflake.class, SNOW_FLAKES);

    Sql       sql         = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("name", "S1"));
    Snowflake target      = orm.get(Snowflake.class, sql);
    Long      snowflakeId = target.getId();

    Snowflake snowflake = orm.getById(Snowflake.class, snowflakeId);
    Assert.assertNotNull(snowflake);

    snowflake = orm.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertNotNull(snowflake);

    Snowflake condition = new Snowflake();
    condition.setName("S1");
    snowflake = orm.getByCriteria(Snowflake.class, condition);
    Assert.assertNotNull(snowflake);

    snowflake = orm.getByCriteria(Snowflake.class, condition, BaseEntity.Update.class);
    Assert.assertNotNull(snowflake);
  }

  @Test
  public void findByXX() {
    initUuids();
    Orm orm = database.createOrm();
    orm.truncate(Uuid.class);
    orm.create(Uuid.class, UUIDS);

    List<String> uuids = UUIDS.stream().map(Uuid::getId).collect(Collectors.toList());

    List<Uuid> uuidList = orm.findByIds(Uuid.class, uuids);
    Assert.assertEquals(uuidList.size(), 3);

    uuidList = orm.findByField(Uuid.class, "name", "S1");
    Assert.assertEquals(uuidList.size(), 1);

    uuidList = orm.findByField(Uuid.class, "id", uuids);
    Assert.assertEquals(uuidList.size(), 3);

    uuidList = orm.findByCond(Uuid.class, Cond.eq("name", "S1"));
    Assert.assertEquals(uuidList.size(), 1);

    Uuid condition = new Uuid();
    condition.setName("S1");

    uuidList = orm.findByCriteria(Uuid.class, condition);
    Assert.assertEquals(uuidList.size(), 1);

    uuidList = orm.findByCriteria(Uuid.class, condition, BaseEntity.Update.class);
    Assert.assertEquals(uuidList.size(), 2);
  }

  @Test
  public void count() {
    initUuids();
    Orm orm = database.createOrm();
    orm.truncate(Uuid.class);
    orm.create(Uuid.class, UUIDS);

    Sql  sql   = Sql.SelectAll().from("tb_uuid");
    long count = orm.count(sql);
    Assert.assertEquals(3L, count);

    count = orm.count(sql.toString(), sql.getParams());
    Assert.assertEquals(3L, count);

    count = orm.countByCond(Uuid.class, Cond.eq("name", "S1"));
    Assert.assertEquals(1L, count);
  }

  @Test
  public void exists() {
    initIdentities();
    Orm orm = database.createOrm();
    orm.truncate(Identity.class);
    orm.create(Identity.class, IDENTITIES);

    Sql     sql    = Sql.SelectAll().from("tb_identity").where(Cond.eq("name", "S1"));
    boolean exists = orm.exists(sql);
    Assert.assertTrue(exists);

    exists = orm.exists(sql.toString(), sql.getParams());
    Assert.assertTrue(exists);

    Identity identity = orm.get(Identity.class, sql);
    exists = orm.exists(Identity.class, identity);
    Assert.assertTrue(exists);

    exists = orm.existsByCond(Identity.class, Cond.eq("name", "S1"));
    Assert.assertTrue(exists);
  }

  @Test
  public void findPageLite() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.create(Snowflake.class, SNOW_FLAKES);

    Sql                 sql  = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("disable", false));
    PageLite<Snowflake> page = orm.findPageLite(Snowflake.class, sql, true, 1, 2);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = orm.findPageLite(Snowflake.class, sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = orm.findPageLite(Snowflake.class, sql, pageable);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = orm.findPageLite(Snowflake.class, sql.toString(), sql.getParams(), pageable);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    PageLite<Record> recordPage = orm.findRecordsPageLite(sql, true, 1, 2);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = orm.findRecordsPageLite(sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = orm.findRecordsPageLite(sql, pageable);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = orm.findRecordsPageLite(sql.toString(), sql.getParams(), pageable);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());
  }

  @Test
  public void findPage() {
    initSnowflakes();
    initUuids();

    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.truncate(Uuid.class);
    orm.create(Snowflake.class, SNOW_FLAKES);
    orm.create(Uuid.class, UUIDS);

    Sql             sql  = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("disable", false));
    Sql             sql2 = Sql.Select("id").from("tb_uuid").where(Cond.eq("disable", false));
    Page<Snowflake> page = orm.findPage(Snowflake.class, sql, true, 1, 2);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = orm.findPage(Snowflake.class, sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = orm.findPage(Snowflake.class, sql, pageable);
    Sql sqlWithId = Sql.Select("id").from("tb_snowflake").where(Cond.eq("disable", false));
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = orm.findPage(Snowflake.class, sql.toString(), sql.getParams(), pageable);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    Page<String> strPage = orm.findPage(String.class, sql2, true, 1, 2);
    Assert.assertEquals(2, strPage.getData().size());
    Assert.assertEquals(2L, strPage.getTotalPages());
    Assert.assertEquals(3L, strPage.getTotalRecords());

    Page<Record> recordPage = orm.findRecordsPage(sql, true, 1, 2);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = orm.findRecordsPage(sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = orm.findRecordsPage(sql, pageable);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = orm.findRecordsPage(sql.toString(), sql.getParams(), pageable);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());
  }

  @Test
  public void execute() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.create(Snowflake.class, SNOW_FLAKES);

    String sql = "update tb_snowflake set disable = ? where name = ?";
    orm.execute(new Sql(sql, List.of(true, "S1")), 3000);
    Snowflake snowflake = orm.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertTrue(snowflake.getDisable());

    orm.execute(sql, List.of(false, "S1"));
    snowflake = orm.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertFalse(snowflake.getDisable());

    orm.execute(new Sql(sql, List.of(true, "S1")), 3000);
    snowflake = orm.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertTrue(snowflake.getDisable());

    orm.execute(sql, List.of(false, "S1"), 3000);
    snowflake = orm.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertFalse(snowflake.getDisable());

    List<Sql> sqlList = new ArrayList<>();
    sqlList.add(new Sql(sql, List.of(true, "S1")));
    sqlList.add(new Sql(sql, List.of(true, "S2")));
    sqlList.add(new Sql(sql, List.of(true, "S3")));

    orm.execute(sqlList);
    List<Snowflake> snowflakes =
        orm.findByField(Snowflake.class, "name", List.of("S1", "S2", "S3"));
    snowflakes.forEach(s -> Assert.assertTrue(s.getDisable()));

    sqlList.clear();
    sqlList.add(new Sql(sql, List.of(false, "S1")));
    sqlList.add(new Sql(sql, List.of(false, "S2")));
    sqlList.add(new Sql(sql, List.of(false, "S3")));

    orm.execute(sqlList);
    snowflakes = orm.findByField(Snowflake.class, "name", List.of("S1", "S2", "S3"));
    snowflakes.forEach(s -> Assert.assertFalse(s.getDisable()));
  }

  @Test
  public void create() {
    initSnowflakes();
    initUuids();
    initIdentities();

    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.truncate(Uuid.class);
    orm.truncate(Identity.class);

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

    orm.create(Snowflake.class, snowflake);
    orm.create(Uuid.class, uuid);
    orm.create(Identity.class, identity);

    Assert.assertTrue(snowflake.getId() > 0L);
    Assert.assertNotNull(uuid.getId());
    Assert.assertTrue(identity.getId() > 0L);

    Assert.assertNotNull(orm.getById(Snowflake.class, snowflake.getId()));
    Assert.assertNotNull(orm.getById(Uuid.class, uuid.getId()));
    Assert.assertNotNull(orm.getById(Identity.class, identity.getId()));
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

    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.truncate(Uuid.class);
    orm.truncate(Identity.class);

    orm.create(Snowflake.class, SNOW_FLAKES);
    orm.create(Uuid.class, UUIDS);
    orm.create(Identity.class, IDENTITIES);

    SNOW_FLAKES.forEach(
        s -> {
          Assert.assertNotNull(s.getId());
          Assert.assertTrue(s.getId() > 0L);
        });

    UUIDS.forEach(
        s -> {
          Assert.assertNotNull(s.getId());
          Assert.assertFalse(s.getId().isEmpty());
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

    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.truncate(Uuid.class);
    orm.truncate(Identity.class);

    orm.create(Snowflake.class, SNOW_FLAKES);
    orm.create(Uuid.class, UUIDS);
    orm.create(Identity.class, IDENTITIES);

    Snowflake snowflake = orm.getByField(Snowflake.class, "name", "S1");
    Uuid      uuid      = orm.getByField(Uuid.class, "name", "S1");
    Identity  identity  = orm.getByField(Identity.class, "name", "S1");

    database
        .tx(
            innerOrm -> {
              snowflake.setPrice(new BigDecimal("999.00"));
              uuid.setPrice(new BigDecimal("999.00"));
              identity.setPrice(new BigDecimal("999.00"));

              innerOrm.update(Snowflake.class, snowflake);
              innerOrm.update(Uuid.class, uuid);
              innerOrm.update(Identity.class, identity);

              Snowflake dbSnowflake = innerOrm.getById(Snowflake.class, snowflake.getId());
              Uuid      dbUuid      = innerOrm.getById(Uuid.class, uuid.getId());
              Identity  dbIdentity  = innerOrm.getById(Identity.class, identity.getId());

              Assert.assertEquals(dbSnowflake.getPrice().compareTo(new BigDecimal("999.00")), 0);
              Assert.assertEquals(dbUuid.getPrice().compareTo(new BigDecimal("999.00")), 0);
              Assert.assertEquals(dbIdentity.getPrice().compareTo(new BigDecimal("999.00")), 0);

              Snowflake condSnowflake = new Snowflake();
              condSnowflake.setId(dbTemplate.getPkGenerator().nextSnowFakeId());
              condSnowflake.setPrice(new BigDecimal("199.00"));
              innerOrm.updateIgnoreNull(Snowflake.class, condSnowflake);

              dbSnowflake = innerOrm.getById(Snowflake.class, snowflake.getId());

              Assert.assertEquals(snowflake.getName(), dbSnowflake.getName());
              Assert.assertEquals(snowflake.getType(), dbSnowflake.getType());
              Assert.assertEquals(snowflake.getDisable(), dbSnowflake.getDisable());
              Assert.assertEquals(snowflake.getCreated(), dbSnowflake.getCreated());
              Assert.assertEquals(snowflake.getPrice().compareTo(dbSnowflake.getPrice()), 0);

              dbUuid.setPrice(new BigDecimal("199.00"));
              innerOrm.update(Uuid.class, dbUuid, new String[]{"price"});

              dbUuid = innerOrm.getById(Uuid.class, uuid.getId());
              Assert.assertEquals(dbUuid.getPrice().compareTo(new BigDecimal("199.00")), 0);

              for (Identity i : IDENTITIES) {
                i.setPrice(new BigDecimal("1099.00"));
              }
              innerOrm.update(Identity.class, IDENTITIES, new String[]{"name","price"});

              List<Identity> identities = innerOrm.find(Identity.class, new Sql("select * from tb_identity"));

              for (Identity i : identities) {
                Assert.assertEquals(i.getPrice().compareTo(new BigDecimal("1099.00")), 0);
              }

              for (Uuid u : UUIDS) {
                u.setType(TypeEnum.DRINK);
              }

              innerOrm.update(Uuid.class, UUIDS);

              List<Uuid> uuids = innerOrm.find(Uuid.class, new Sql("select * from tb_uuid"));
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

    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.truncate(Uuid.class);
    orm.truncate(Identity.class);

    Snowflake snowflake = SNOW_FLAKES.get(0);
    Uuid      uuid      = UUIDS.get(0);
    Identity  identity  = IDENTITIES.get(0);

    Snowflake dbSnowflake = orm.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Uuid      dbUuid      = orm.getByCond(Uuid.class, Cond.eq("name", "S1"));
    Identity  dbIdentity  = orm.getByCond(Identity.class, Cond.eq("name", "S1"));

    Assert.assertNull(dbSnowflake);
    Assert.assertNull(dbUuid);
    Assert.assertNull(dbIdentity);

    orm.createOrUpdate(Snowflake.class, snowflake);
    orm.createOrUpdate(Uuid.class, uuid);
    orm.createOrUpdate(Identity.class, identity);

    dbSnowflake = orm.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    dbUuid = orm.getByCond(Uuid.class, Cond.eq("name", "S1"));
    dbIdentity = orm.getByCond(Identity.class, Cond.eq("name", "S1"));

    Assert.assertNotNull(dbSnowflake);
    Assert.assertNotNull(dbUuid);
    Assert.assertNotNull(dbIdentity);

    snowflake.setPrice(new BigDecimal("999.99"));
    uuid.setPrice(new BigDecimal("999.99"));
    identity.setPrice(new BigDecimal("999.99"));

    orm.createOrUpdate(Snowflake.class, snowflake);
    orm.createOrUpdate(Uuid.class, uuid);
    orm.createOrUpdate(Identity.class, identity);

    dbSnowflake = orm.getByCond(Snowflake.class, Cond.eq("name", "S1"));
    dbUuid = orm.getByCond(Uuid.class, Cond.eq("name", "S1"));
    dbIdentity = orm.getByCond(Identity.class, Cond.eq("name", "S1"));

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

    Orm orm = database.createOrm();
    orm.truncate(Snowflake.class);
    orm.truncate(Uuid.class);
    orm.truncate(Identity.class);
    orm.truncate(LogicDelete.class);

    orm.create(Snowflake.class, SNOW_FLAKES);
    orm.create(Uuid.class, UUIDS);
    orm.create(Identity.class, IDENTITIES);
    orm.create(LogicDelete.class, LOGIC_DELETES);

    orm.delete(Snowflake.class, SNOW_FLAKES.get(0));
    orm.deleteById(Snowflake.class, SNOW_FLAKES.get(1).getId());
    orm.deleteByCond(Snowflake.class, Cond.eq("name", "S3"));

    orm.delete(Uuid.class, UUIDS);

    List<Long> ids = IDENTITIES.stream().map(Identity::getId).collect(Collectors.toList());
    orm.deleteByIds(Identity.class, ids);

    orm.deleteByCond(LogicDelete.class, Cond.eq("name", "S1"));

    long c1 = orm.count(new Sql("SELECT * FROM tb_snowflake"));
    long c2 = orm.count(new Sql("SELECT * FROM tb_uuid"));
    long c3 = orm.count(new Sql("SELECT * FROM tb_identity"));
    long c4 = orm.count(new Sql("SELECT * FROM tb_logic_delete"));

    Assert.assertEquals(c1, 0);
    Assert.assertEquals(c2, 0);
    Assert.assertEquals(c3, 0);
    Assert.assertEquals(c4, 3);
  }
}
