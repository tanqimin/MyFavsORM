package work.myfavs.framework.orm;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import work.myfavs.framework.orm.entity.BaseEntity;
import work.myfavs.framework.orm.entity.Identity;
import work.myfavs.framework.orm.entity.Snowflake;
import work.myfavs.framework.orm.entity.Uuid;
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
import work.myfavs.framework.orm.util.JsonUtil;
import work.myfavs.framework.orm.util.func.ThrowingFunction;

public class DBTest extends AbstractTest
    implements ISnowflakeTest, IIdentityTest, IUuidTest, ILogicDeleteTest {

  private static final Logger log = LoggerFactory.getLogger(DBTest.class);

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

    initSnowflakes();
    DB.conn().truncate(Snowflake.class);

    ThrowingFunction<DB, List<Snowflake>, SQLException> func1 =
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

    List<Snowflake> txSnowflakes = DB.conn(dbTemplate).tx(func1);

    DB.conn(dbTemplate)
        .tx(
            db -> {
              db.delete(Snowflake.class, txSnowflakes);
              Savepoint savepoint = db.setSavepoint();
              log.info("savepoint: {}", savepoint.getSavepointId());
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
    DB.conn().truncate(Snowflake.class);
    DB.conn().create(Snowflake.class, SNOW_FLAKES);

    List<Snowflake> snowflakes = DB.conn().find(Snowflake.class, "SELECT * FROM tb_snowflake");
    Assert.assertEquals(3, snowflakes.size());

    snowflakes =
        DB.conn().find(Snowflake.class, "SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1"));
    Assert.assertEquals(1, snowflakes.size());

    snowflakes = DB.conn().find(Snowflake.class, new Sql("SELECT * FROM tb_snowflake"));
    Assert.assertEquals(3, snowflakes.size());

    List<Record> records =
        DB.conn().find(new Sql("SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1")));
    Assert.assertEquals(1, records.size());

    records = DB.conn().find("SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1"));
    Assert.assertEquals(1, records.size());
  }

  @Test
  public void findMap() {
    initSnowflakes();
    DB.conn().truncate(Snowflake.class);
    DB.conn().create(Snowflake.class, SNOW_FLAKES);

    Map<Long, Snowflake> result =
        DB.conn()
            .findMap(
                Snowflake.class,
                "id",
                "SELECT * FROM tb_snowflake WHERE disable = ?",
                List.of(false));

    result.forEach(
        (k, v) -> {
          log.info("key: {}, value: {}", k, JsonUtil.toJsonStr(v));
          Assert.assertTrue(k > 0L);
        });

    result =
        DB.conn()
            .findMap(
                Snowflake.class,
                "id",
                new Sql("SELECT * FROM tb_snowflake WHERE disable = ?", List.of(false)));
    result.forEach(
        (k, v) -> {
          log.info("key: {}, value: {}", k, JsonUtil.toJsonStr(v));
          Assert.assertTrue(k > 0L);
        });
  }

  @Test
  public void findTop() {
    initSnowflakes();
    DB.conn().truncate(Snowflake.class);
    DB.conn().create(Snowflake.class, SNOW_FLAKES);

    Sql sql = Sql.SelectAll().from("tb_snowflake");
    List<Snowflake> snowflake = DB.conn().findTop(Snowflake.class, 1, sql);
    Assert.assertEquals(1, snowflake.size());

    snowflake = DB.conn().findTop(Snowflake.class, 1, sql.toString(), sql.getParams());
    Assert.assertEquals(1, snowflake.size());

    List<Record> records = DB.conn().findTop(1, sql);

    Assert.assertEquals(1, records.size());

    records = DB.conn().findTop(1, sql.toString(), sql.getParams());
    Assert.assertEquals(1, records.size());
  }

  @Test
  public void get() {
    initSnowflakes();
    DB.conn().truncate(Snowflake.class);
    DB.conn().create(Snowflake.class, SNOW_FLAKES);

    Sql sql = Sql.SelectAll().from("tb_snowflake");
    Snowflake snowflake = DB.conn().get(Snowflake.class, sql);
    Assert.assertNotNull(snowflake);
    snowflake = DB.conn().get(Snowflake.class, sql.toString(), sql.getParams());
    Assert.assertNotNull(snowflake);
    Record record = DB.conn().get(sql);
    Assert.assertNotNull(record);
    record = DB.conn().get(sql.toString(), sql.getParams());
    Assert.assertNotNull(record);
  }

  @Test
  public void getByXX() {
    initSnowflakes();
    DB.conn().truncate(Snowflake.class);
    DB.conn().create(Snowflake.class, SNOW_FLAKES);

    Sql sql = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("name", "S1"));
    Snowflake target = DB.conn().get(Snowflake.class, sql);
    Long snowflakeId = target.getId();

    Snowflake snowflake = DB.conn().getById(Snowflake.class, snowflakeId);
    Assert.assertNotNull(snowflake);

    snowflake = DB.conn().getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertNotNull(snowflake);

    Snowflake condition = new Snowflake();
    condition.setName("S1");
    snowflake = DB.conn().getByCriteria(Snowflake.class, condition);
    Assert.assertNotNull(snowflake);

    snowflake = DB.conn().getByCriteria(Snowflake.class, condition, BaseEntity.Update.class);
    Assert.assertNotNull(snowflake);
  }

  @Test
  public void findByXX() {
    initUuids();
    DB.conn().truncate(Uuid.class);
    DB.conn().create(Uuid.class, UUIDS);

    List<String> uuids = UUIDS.stream().map(Uuid::getId).collect(Collectors.toList());

    List<Uuid> uuidList = DB.conn().findByIds(Uuid.class, uuids);
    Assert.assertEquals(uuidList.size(), 3);

    uuidList = DB.conn().findByField(Uuid.class, "name", "S1");
    Assert.assertEquals(uuidList.size(), 1);

    uuidList = DB.conn().findByField(Uuid.class, "id", uuids);
    Assert.assertEquals(uuidList.size(), 3);

    uuidList = DB.conn().findByCond(Uuid.class, Cond.eq("name", "S1"));
    Assert.assertEquals(uuidList.size(), 1);

    Uuid condition = new Uuid();
    condition.setName("S1");

    uuidList = DB.conn().findByCriteria(Uuid.class, condition);
    Assert.assertEquals(uuidList.size(), 1);

    uuidList = DB.conn().findByCriteria(Uuid.class, condition, BaseEntity.Update.class);
    Assert.assertEquals(uuidList.size(), 2);
  }

  @Test
  public void count() {
    initUuids();
    DB.conn().truncate(Uuid.class);
    DB.conn().create(Uuid.class, UUIDS);

    Sql sql = Sql.SelectAll().from("tb_uuid");
    long count = DB.conn().count(sql);
    Assert.assertEquals(3L, count);

    count = DB.conn().count(sql.toString(), sql.getParams());
    Assert.assertEquals(3L, count);

    count = DB.conn().countByCond(Uuid.class, Cond.eq("name", "S1"));
    Assert.assertEquals(1L, count);
  }

  @Test
  public void exists() {
    initIdentities();
    DB.conn().truncate(Identity.class);
    DB.conn().create(Identity.class, IDENTITIES);

    Sql sql = Sql.SelectAll().from("tb_identity").where(Cond.eq("name", "S1"));
    boolean exists = DB.conn().exists(sql);
    Assert.assertTrue(exists);

    exists = DB.conn().exists(sql.toString(), sql.getParams());
    Assert.assertTrue(exists);

    Identity identity = DB.conn().get(Identity.class, sql);
    exists = DB.conn().exists(Identity.class, identity);
    Assert.assertTrue(exists);

    exists = DB.conn().existsByCond(Identity.class, Cond.eq("name", "S1"));
    Assert.assertTrue(exists);
  }

  @Test
  public void findPageLite() {
    initSnowflakes();
    DB.conn().truncate(Snowflake.class);
    DB.conn().create(Snowflake.class, SNOW_FLAKES);

    Sql sql = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("disable", false));
    PageLite<Snowflake> page = DB.conn().findPageLite(Snowflake.class, sql, true, 1, 2);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = DB.conn().findPageLite(Snowflake.class, sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = DB.conn().findPageLite(Snowflake.class, sql, pageable);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = DB.conn().findPageLite(Snowflake.class, sql.toString(), sql.getParams(), pageable);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    PageLite<Record> recordPage = DB.conn().findPageLite(sql, true, 1, 2);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = DB.conn().findPageLite(sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = DB.conn().findPageLite(sql, pageable);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());

    recordPage = DB.conn().findPageLite(sql.toString(), sql.getParams(), pageable);
    Assert.assertTrue(recordPage.isHasNext());
    Assert.assertEquals(2, recordPage.getData().size());
  }

  @Test
  public void findPage() {
    initSnowflakes();
    initUuids();
    DB.conn().truncate(Snowflake.class);
    DB.conn().truncate(Uuid.class);
    DB.conn().create(Snowflake.class, SNOW_FLAKES);
    DB.conn().create(Uuid.class, UUIDS);

    Sql sql = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("disable", false));
    Sql sql2 = Sql.Select("id").from("tb_uuid").where(Cond.eq("disable", false));
    Page<Snowflake> page = DB.conn().findPage(Snowflake.class, sql, true, 1, 2);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = DB.conn().findPage(Snowflake.class, sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = DB.conn().findPage(Snowflake.class, sql, pageable);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = DB.conn().findPage(Snowflake.class, sql.toString(), sql.getParams(), pageable);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    Page<String> strPage = DB.conn().findPage(String.class, sql2, true, 1, 2);
    Assert.assertEquals(2, strPage.getData().size());
    Assert.assertEquals(2L, strPage.getTotalPages());
    Assert.assertEquals(3L, strPage.getTotalRecords());

    Page<Record> recordPage = DB.conn().findPage(sql, true, 1, 2);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = DB.conn().findPage(sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = DB.conn().findPage(sql, pageable);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());

    recordPage = DB.conn().findPage(sql.toString(), sql.getParams(), pageable);
    Assert.assertEquals(2, recordPage.getData().size());
    Assert.assertEquals(2L, recordPage.getTotalPages());
    Assert.assertEquals(3L, recordPage.getTotalRecords());
  }

  @Test
  public void execute() {
    initSnowflakes();
    DB.conn().truncate(Snowflake.class);
    DB.conn().create(Snowflake.class, SNOW_FLAKES);

    String sql = "update tb_snowflake set disable = ? where name = ?";
    DB.conn().execute(new Sql(sql, List.of(true, "S1")), 3000);
    Snowflake snowflake = DB.conn().getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertTrue(snowflake.getDisable());

    DB.conn().execute(sql, List.of(false, "S1"));
    snowflake = DB.conn().getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertFalse(snowflake.getDisable());

    DB.conn().execute(new Sql(sql, List.of(true, "S1")), 3000);
    snowflake = DB.conn().getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertTrue(snowflake.getDisable());

    DB.conn().execute(sql, List.of(false, "S1"), 3000);
    snowflake = DB.conn().getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Assert.assertFalse(snowflake.getDisable());

    List<Sql> sqlList = new ArrayList<>();
    sqlList.add(new Sql(sql, List.of(true, "S1")));
    sqlList.add(new Sql(sql, List.of(true, "S2")));
    sqlList.add(new Sql(sql, List.of(true, "S3")));

    DB.conn().execute(sqlList);
    List<Snowflake> snowflakes =
        DB.conn().findByField(Snowflake.class, "name", List.of("S1", "S2", "S3"));
    snowflakes.forEach(s -> Assert.assertTrue(s.getDisable()));

    sqlList.clear();
    sqlList.add(new Sql(sql, List.of(false, "S1")));
    sqlList.add(new Sql(sql, List.of(false, "S2")));
    sqlList.add(new Sql(sql, List.of(false, "S3")));

    DB.conn().execute(sqlList);
    snowflakes = DB.conn().findByField(Snowflake.class, "name", List.of("S1", "S2", "S3"));
    snowflakes.forEach(s -> Assert.assertFalse(s.getDisable()));
  }

  @Test
  public void create() {
    initSnowflakes();
    initUuids();
    initIdentities();

    DB.conn().truncate(Snowflake.class);
    DB.conn().truncate(Uuid.class);
    DB.conn().truncate(Identity.class);

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

    DB.conn().create(Snowflake.class, snowflake);
    DB.conn().create(Uuid.class, uuid);
    DB.conn().create(Identity.class, identity);

    Assert.assertTrue(snowflake.getId() > 0L);
    Assert.assertNotNull(uuid.getId());
    Assert.assertTrue(identity.getId() > 0L);

    Assert.assertNotNull(DB.conn().getById(Snowflake.class, snowflake.getId()));
    Assert.assertNotNull(DB.conn().getById(Uuid.class, uuid.getId()));
    Assert.assertNotNull(DB.conn().getById(Identity.class, identity.getId()));
  }

  @Test
  public void testId() {
    long snowFlakeId = DB.conn().snowFlakeId();
    String uuid = DB.conn().uuid();
    Assert.assertTrue(snowFlakeId > 0);
    Assert.assertNotNull(uuid);
  }

  @Test
  public void testCreate() {
    initSnowflakes();
    initUuids();
    initIdentities();

    DB.conn().truncate(Snowflake.class);
    DB.conn().truncate(Uuid.class);
    DB.conn().truncate(Identity.class);

    DB.conn().create(Snowflake.class, SNOW_FLAKES);
    DB.conn().create(Uuid.class, UUIDS);
    DB.conn().create(Identity.class, IDENTITIES);

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

    DB.conn().truncate(Snowflake.class);
    DB.conn().truncate(Uuid.class);
    DB.conn().truncate(Identity.class);

    DB.conn().create(Snowflake.class, SNOW_FLAKES);
    DB.conn().create(Uuid.class, UUIDS);
    DB.conn().create(Identity.class, IDENTITIES);

    Snowflake snowflake = DB.conn().getByField(Snowflake.class, "name", "S1");
    Uuid uuid = DB.conn().getByField(Uuid.class, "name", "S1");
    Identity identity = DB.conn().getByField(Identity.class, "name", "S1");

    DB.conn()
        .tx(
            db -> {
              snowflake.setPrice(new BigDecimal("999.00"));
              uuid.setPrice(new BigDecimal("999.00"));
              identity.setPrice(new BigDecimal("999.00"));

              db.update(Snowflake.class, snowflake);
              db.update(Uuid.class, uuid);
              db.update(Identity.class, identity);

              Snowflake dbSnowflake = db.getById(Snowflake.class, snowflake.getId());
              Uuid dbUuid = db.getById(Uuid.class, uuid.getId());
              Identity dbIdentity = db.getById(Identity.class, identity.getId());

              Assert.assertEquals(dbSnowflake.getPrice().compareTo(new BigDecimal("999.00")), 0);
              Assert.assertEquals(dbUuid.getPrice().compareTo(new BigDecimal("999.00")), 0);
              Assert.assertEquals(dbIdentity.getPrice().compareTo(new BigDecimal("999.00")), 0);

              Snowflake condSnowflake = new Snowflake();
              condSnowflake.setId(db.snowFlakeId());
              condSnowflake.setPrice(new BigDecimal("199.00"));
              db.updateIgnoreNull(Snowflake.class, condSnowflake);

              dbSnowflake = db.getById(Snowflake.class, snowflake.getId());

              Assert.assertEquals(snowflake.getName(), dbSnowflake.getName());
              Assert.assertEquals(snowflake.getType(), dbSnowflake.getType());
              Assert.assertEquals(snowflake.getDisable(), dbSnowflake.getDisable());
              Assert.assertEquals(snowflake.getCreated(), dbSnowflake.getCreated());
              Assert.assertEquals(snowflake.getPrice().compareTo(dbSnowflake.getPrice()), 0);

              dbUuid.setPrice(new BigDecimal("199.00"));
              db.update(Uuid.class, dbUuid, new String[] {"price"});

              dbUuid = db.getById(Uuid.class, uuid.getId());
              Assert.assertEquals(dbUuid.getPrice().compareTo(new BigDecimal("199.00")), 0);

              for (Identity i : IDENTITIES) {
                i.setPrice(new BigDecimal("1099.00"));
              }
              db.update(Identity.class, IDENTITIES, new String[] {"price"});

              List<Identity> identities = db.find(Identity.class, "select * from tb_identity");

              for (Identity i : identities) {
                Assert.assertEquals(i.getPrice().compareTo(new BigDecimal("1099.00")), 0);
              }

              for (Uuid u : UUIDS) {
                u.setType(TypeEnum.DRINK);
              }

              db.update(Uuid.class, UUIDS);

              List<Uuid> uuids = db.find(Uuid.class, "select * from tb_uuid");
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

    DB.conn().truncate(Snowflake.class);
    DB.conn().truncate(Uuid.class);
    DB.conn().truncate(Identity.class);

    Snowflake snowflake = SNOW_FLAKES.get(0);
    Uuid uuid = UUIDS.get(0);
    Identity identity = IDENTITIES.get(0);

    Snowflake dbSnowflake = DB.conn().getByCond(Snowflake.class, Cond.eq("name", "S1"));
    Uuid dbUuid = DB.conn().getByCond(Uuid.class, Cond.eq("name", "S1"));
    Identity dbIdentity = DB.conn().getByCond(Identity.class, Cond.eq("name", "S1"));

    Assert.assertNull(dbSnowflake);
    Assert.assertNull(dbUuid);
    Assert.assertNull(dbIdentity);

    DB.conn().createOrUpdate(Snowflake.class, snowflake);
    DB.conn().createOrUpdate(Uuid.class, uuid);
    DB.conn().createOrUpdate(Identity.class, identity);

    dbSnowflake = DB.conn().getByCond(Snowflake.class, Cond.eq("name", "S1"));
    dbUuid = DB.conn().getByCond(Uuid.class, Cond.eq("name", "S1"));
    dbIdentity = DB.conn().getByCond(Identity.class, Cond.eq("name", "S1"));

    Assert.assertNotNull(dbSnowflake);
    Assert.assertNotNull(dbUuid);
    Assert.assertNotNull(dbIdentity);

    snowflake.setPrice(new BigDecimal("999.99"));
    uuid.setPrice(new BigDecimal("999.99"));
    identity.setPrice(new BigDecimal("999.99"));

    DB.conn().createOrUpdate(Snowflake.class, snowflake);
    DB.conn().createOrUpdate(Uuid.class, uuid);
    DB.conn().createOrUpdate(Identity.class, identity);

    dbSnowflake = DB.conn().getByCond(Snowflake.class, Cond.eq("name", "S1"));
    dbUuid = DB.conn().getByCond(Uuid.class, Cond.eq("name", "S1"));
    dbIdentity = DB.conn().getByCond(Identity.class, Cond.eq("name", "S1"));

    Assert.assertEquals(dbSnowflake.getPrice().compareTo(new BigDecimal("999.99")), 0);
    Assert.assertEquals(dbUuid.getPrice().compareTo(new BigDecimal("999.99")), 0);
    Assert.assertEquals(dbIdentity.getPrice().compareTo(new BigDecimal("999.99")), 0);
  }

  @Test
  public void delete() {
    initSnowflakes();
    initUuids();
    initIdentities();

    DB.conn().truncate(Snowflake.class);
    DB.conn().truncate(Uuid.class);
    DB.conn().truncate(Identity.class);

    DB.conn().create(Snowflake.class, SNOW_FLAKES);
    DB.conn().create(Uuid.class, UUIDS);
    DB.conn().create(Identity.class, IDENTITIES);

    DB.conn().delete(Snowflake.class, SNOW_FLAKES.get(0));
    DB.conn().deleteById(Snowflake.class, SNOW_FLAKES.get(1).getId());
    DB.conn().deleteByCond(Snowflake.class, Cond.eq("name", "S3"));

    DB.conn().delete(Uuid.class, UUIDS);

    List<Long> ids = IDENTITIES.stream().map(Identity::getId).collect(Collectors.toList());
    DB.conn().deleteByIds(Identity.class, ids);

    long c1 = DB.conn().count(new Sql("SELECT * FROM tb_snowflake"));
    long c2 = DB.conn().count(new Sql("SELECT * FROM tb_uuid"));
    long c3 = DB.conn().count(new Sql("SELECT * FROM tb_identity"));

    Assert.assertEquals(c1, 0);
    Assert.assertEquals(c2, 0);
    Assert.assertEquals(c3, 0);
  }
}
