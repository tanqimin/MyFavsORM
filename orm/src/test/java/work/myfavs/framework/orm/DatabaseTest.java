package work.myfavs.framework.orm;

import org.junit.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.entity.*;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.entity.test.*;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.orm.Orm;
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
    implements ISnowflakeTest, IIdentityTest, IUuidTest, ILogicDeleteTest, IAssignedTest {

  final IPageable pageable =
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

  @Test
  public void shouldRollbackToSavepointAndCommitPartialData() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    ThrowingFunction<Orm, List<SnowflakeExample>, SQLException> func1 =
        innerOrm -> {
          innerOrm.create(SnowflakeExample.class, SNOW_FLAKES.get(0));

          Savepoint sp1 = database.setSavepoint("sp1");

          try {
            innerOrm.create(SnowflakeExample.class, SNOW_FLAKES.get(1));
            throw new Exception("111");
          } catch (Exception e) {
            database.rollback(sp1);
          }

          innerOrm.create(SnowflakeExample.class, SNOW_FLAKES.get(2));

          return innerOrm.find(SnowflakeExample.class, new Sql("SELECT * FROM tb_snowflake"));
        };

    List<SnowflakeExample> txSnowflakes = database.tx(func1);

    database
        .tx(
            innerOrm -> {
              innerOrm.delete(SnowflakeExample.class, txSnowflakes);
              Savepoint savepoint = database.setSavepoint("afterDelete");
              database.rollback();
            });
    Assert.assertEquals(2, txSnowflakes.size());
  }

  @Test
  public void shouldFindEntitiesAndRecordsBySql() {
    initSnowflakes();
    initAssigned();

    database.tx(orm -> {
      orm.truncate(SnowflakeExample.class);
      orm.truncate(AssignedExample.class);

      orm.create(SnowflakeExample.class, SNOW_FLAKES);
      orm.create(AssignedExample.class, ASSIGNEDS);

      List<SnowflakeExample> snowflakes = orm.find(SnowflakeExample.class, new Sql("SELECT * FROM tb_snowflake"));
      List<AssignedExample>  assigneds  = orm.find(AssignedExample.class, new Sql("SELECT * FROM tb_assigned"));
      Assert.assertEquals(3, snowflakes.size());
      Assert.assertEquals(3, assigneds.size());

      snowflakes = orm.find(SnowflakeExample.class, "SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1"));
      Assert.assertEquals(1, snowflakes.size());

      snowflakes = orm.find(SnowflakeExample.class, new Sql("SELECT * FROM tb_snowflake"));
      Assert.assertEquals(3, snowflakes.size());

      snowflakes = orm.find(SnowflakeExample.class, new Sql("SELECT id, type, name FROM tb_snowflake"));
      Assert.assertEquals(3, snowflakes.size());

      List<Record> records = orm.findRecords(new Sql("SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1")));
      Assert.assertEquals(1, records.size());

      records = orm.findRecords("SELECT * FROM tb_snowflake WHERE name = ?", List.of("S1"));
      Assert.assertEquals(1, records.size());
    });
  }

  @Test
  public void shouldFindMapWithKeyColumn() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    Map<Long, SnowflakeExample> result = orm.findMap(
        SnowflakeExample.class,
        "id",
        "SELECT * FROM tb_snowflake WHERE disable = ?",
        List.of(false));

    result.forEach((k, v) -> Assert.assertTrue(k > 0L));

    result = orm.findMap(
        SnowflakeExample.class,
        "id",
        new Sql("SELECT * FROM tb_snowflake WHERE disable = ?", List.of(false)));
    result.forEach((k, v) -> Assert.assertTrue(k > 0L));
  }

  @Test
  public void shouldFindTopRecords() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    Sql                    sql       = Sql.SelectAll().from("tb_snowflake");
    List<SnowflakeExample> snowflake = orm.findTop(SnowflakeExample.class, 1, sql);
    Assert.assertEquals(1, snowflake.size());

    snowflake = orm.findTop(SnowflakeExample.class, 1, sql.toString(), sql.getParams());
    Assert.assertEquals(1, snowflake.size());

    List<Record> records = orm.findTopRecords(1, sql);

    Assert.assertEquals(1, records.size());

    records = orm.findTopRecords(1, sql.toString(), sql.getParams());
    Assert.assertEquals(1, records.size());
  }

  @Test
  public void shouldGetSingleEntityOrRecord() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    Sql              sql       = Sql.SelectAll().from("tb_snowflake");
    SnowflakeExample snowflake = orm.get(SnowflakeExample.class, sql);
    Assert.assertNotNull(snowflake);
    snowflake = orm.get(SnowflakeExample.class, sql.toString(), sql.getParams());
    Assert.assertNotNull(snowflake);
    Record record = orm.getRecord(sql);
    Assert.assertNotNull(record);
    record = orm.getRecord(sql.toString(), sql.getParams());
    Assert.assertNotNull(record);
  }

  @Test
  public void shouldGetEntityByIdCondAndCriteria() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    Sql              sql         = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("name", "S1"));
    SnowflakeExample target      = orm.get(SnowflakeExample.class, sql);
    Long             snowflakeId = target.getId();

    SnowflakeExample snowflake = orm.getById(SnowflakeExample.class, snowflakeId);
    Assert.assertNotNull(snowflake);

    snowflake = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    Assert.assertNotNull(snowflake);

    SnowflakeExample condition = new SnowflakeExample();
    condition.setName("S1");
    snowflake = orm.getByCriteria(SnowflakeExample.class, condition);
    Assert.assertNotNull(snowflake);

    snowflake = orm.getByCriteria(SnowflakeExample.class, condition, BaseEntity.Update.class);
    Assert.assertNotNull(snowflake);
  }

  @Test
  public void shouldFindEntitiesByMultipleMethods() {
    initUuids();
    Orm orm = database.createOrm();
    orm.truncate(UuidExample.class);
    orm.create(UuidExample.class, UUIDS);

    List<String> uuids = UUIDS.stream().map(UuidExample::getId).collect(Collectors.toList());

    List<UuidExample> uuidList = orm.findByIds(UuidExample.class, uuids);
    Assert.assertEquals(3, uuidList.size());

    uuidList = orm.findByField(UuidExample.class, "name", "S1");
    Assert.assertEquals(1, uuidList.size());

    uuidList = orm.findByField(UuidExample.class, "id", uuids);
    Assert.assertEquals(3, uuidList.size());

    uuidList = orm.findByCond(UuidExample.class, Cond.eq("name", "S1"));
    Assert.assertEquals(1, uuidList.size());

    UuidExample condition = new UuidExample();
    condition.setName("S1");

    uuidList = orm.findByCriteria(UuidExample.class, condition);
    Assert.assertEquals(1, uuidList.size());

    uuidList = orm.findByCriteria(UuidExample.class, condition, BaseEntity.Update.class);
    Assert.assertEquals(2, uuidList.size());
  }

  @Test
  public void shouldCountRecords() {
    initUuids();
    Orm orm = database.createOrm();
    orm.truncate(UuidExample.class);
    orm.create(UuidExample.class, UUIDS);

    Sql  sql   = Sql.SelectAll().from("tb_uuid");
    long count = orm.count(sql);
    Assert.assertEquals(3L, count);

    count = orm.count(sql.toString(), sql.getParams());
    Assert.assertEquals(3L, count);

    count = orm.countByCond(UuidExample.class, Cond.eq("name", "S1"));
    Assert.assertEquals(1L, count);
  }

  @Test
  public void shouldCheckEntityExistence() {
    initIdentities();
    Orm orm = database.createOrm();
    orm.truncate(IdentityExample.class);
    orm.create(IdentityExample.class, IDENTITIES);

    Sql     sql    = Sql.SelectAll().from("tb_identity").where(Cond.eq("name", "S1"));
    boolean exists = orm.exists(sql);
    Assert.assertTrue(exists);

    exists = orm.exists(sql.toString(), sql.getParams());
    Assert.assertTrue(exists);

    IdentityExample identity = orm.get(IdentityExample.class, sql);
    exists = orm.exists(IdentityExample.class, identity);
    Assert.assertTrue(exists);

    exists = orm.existsByCond(IdentityExample.class, Cond.eq("name", "S1"));
    Assert.assertTrue(exists);
  }

  @Test
  public void shouldFindPageLite() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    Sql                        sql  = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("disable", false));
    PageLite<SnowflakeExample> page = orm.findPageLite(SnowflakeExample.class, sql, true, 1, 2);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = orm.findPageLite(SnowflakeExample.class, sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = orm.findPageLite(SnowflakeExample.class, sql, pageable);
    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());

    page = orm.findPageLite(SnowflakeExample.class, sql.toString(), sql.getParams(), pageable);
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
  public void shouldFindPage() {
    initSnowflakes();
    initUuids();

    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.truncate(UuidExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);
    orm.create(UuidExample.class, UUIDS);

    Sql                    sql  = Sql.SelectAll().from("tb_snowflake").where(Cond.eq("disable", false));
    Sql                    sql2 = Sql.Select("id").from("tb_uuid").where(Cond.eq("disable", false));
    Page<SnowflakeExample> page = orm.findPage(SnowflakeExample.class, sql, true, 1, 2);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = orm.findPage(SnowflakeExample.class, sql.toString(), sql.getParams(), true, 1, 2);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = orm.findPage(SnowflakeExample.class, sql, pageable);
    Assert.assertEquals(2, page.getData().size());
    Assert.assertEquals(2L, page.getTotalPages());
    Assert.assertEquals(3L, page.getTotalRecords());

    page = orm.findPage(SnowflakeExample.class, sql.toString(), sql.getParams(), pageable);
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
  public void shouldExecuteUpdateSql() {
    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    String sql = "update tb_snowflake set disable = ? where name = ?";
    orm.execute(new Sql(sql, List.of(true, "S1")), 3000);
    SnowflakeExample snowflake = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    Assert.assertTrue(snowflake.getDisable());

    orm.execute(sql, List.of(false, "S1"));
    snowflake = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    Assert.assertFalse(snowflake.getDisable());

    orm.execute(new Sql(sql, List.of(true, "S1")), 3000);
    snowflake = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    Assert.assertTrue(snowflake.getDisable());

    orm.execute(sql, List.of(false, "S1"), 3000);
    snowflake = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    Assert.assertFalse(snowflake.getDisable());

    List<Sql> sqlList = new ArrayList<>();
    sqlList.add(new Sql(sql, List.of(true, "S1")));
    sqlList.add(new Sql(sql, List.of(true, "S2")));
    sqlList.add(new Sql(sql, List.of(true, "S3")));

    orm.execute(sqlList);
    List<SnowflakeExample> snowflakes =
        orm.findByField(SnowflakeExample.class, "name", List.of("S1", "S2", "S3"));
    snowflakes.forEach(s -> Assert.assertTrue(s.getDisable()));

    sqlList.clear();
    sqlList.add(new Sql(sql, List.of(false, "S1")));
    sqlList.add(new Sql(sql, List.of(false, "S2")));
    sqlList.add(new Sql(sql, List.of(false, "S3")));

    orm.execute(sqlList);
    snowflakes = orm.findByField(SnowflakeExample.class, "name", List.of("S1", "S2", "S3"));
    snowflakes.forEach(s -> Assert.assertFalse(s.getDisable()));
  }

  @Test
  public void shouldCreateEntityAndReturnGeneratedIds() {
    initSnowflakes();
    initUuids();
    initIdentities();

    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.truncate(UuidExample.class);
    orm.truncate(IdentityExample.class);

    SnowflakeExample snowflake = new SnowflakeExample();
    snowflake.setCreated(new Date());
    snowflake.setName("S1");
    snowflake.setPrice(new BigDecimal("199.00"));
    snowflake.setType(TypeEnum.FOOD);

    UuidExample uuid = new UuidExample();
    uuid.setCreated(new Date());
    uuid.setName("S1");
    uuid.setPrice(new BigDecimal("199.00"));
    uuid.setType(TypeEnum.FOOD);

    IdentityExample identity = new IdentityExample();
    identity.setCreated(new Date());
    identity.setName("S1");
    identity.setPrice(new BigDecimal("199.00"));
    identity.setType(TypeEnum.FOOD);

    orm.create(SnowflakeExample.class, snowflake);
    orm.create(UuidExample.class, uuid);
    orm.create(IdentityExample.class, identity);

    Assert.assertTrue(snowflake.getId() > 0L);
    Assert.assertNotNull(uuid.getId());
    Assert.assertTrue(identity.getId() > 0L);

    Assert.assertNotNull(orm.getById(SnowflakeExample.class, snowflake.getId()));
    Assert.assertNotNull(orm.getById(UuidExample.class, uuid.getId()));
    Assert.assertNotNull(orm.getById(IdentityExample.class, identity.getId()));
  }

  @Test
  public void shouldGenerateSnowflakeIdAndUuid() {
    long   snowFlakeId = dbTemplate.getPkGenerator().nextSnowFakeId();
    String uuid        = dbTemplate.getPkGenerator().nextUUID();
    Assert.assertTrue(snowFlakeId > 0);
    Assert.assertNotNull(uuid);
  }

  @Test
  public void shouldCreateMultipleEntitiesWithGeneratedIds() {
    initSnowflakes();
    initUuids();
    initIdentities();

    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.truncate(UuidExample.class);
    orm.truncate(IdentityExample.class);

    orm.create(SnowflakeExample.class, SNOW_FLAKES);
    orm.create(UuidExample.class, UUIDS);
    orm.create(IdentityExample.class, IDENTITIES);

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
  public void shouldUpdateEntitiesWithVariousStrategies() {
    initSnowflakes();
    initUuids();
    initIdentities();

    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.truncate(UuidExample.class);
    orm.truncate(IdentityExample.class);

    orm.create(SnowflakeExample.class, SNOW_FLAKES);
    orm.create(UuidExample.class, UUIDS);
    orm.create(IdentityExample.class, IDENTITIES);

    SnowflakeExample snowflake = orm.getByField(SnowflakeExample.class, "name", "S1");
    UuidExample      uuid      = orm.getByField(UuidExample.class, "name", "S1");
    IdentityExample  identity  = orm.getByField(IdentityExample.class, "name", "S1");

    database
        .tx(
            innerOrm -> {
              snowflake.setPrice(new BigDecimal("999.00"));
              uuid.setPrice(new BigDecimal("999.00"));
              identity.setPrice(new BigDecimal("999.00"));

              innerOrm.update(SnowflakeExample.class, snowflake);
              innerOrm.update(UuidExample.class, uuid);
              innerOrm.update(IdentityExample.class, identity);

              SnowflakeExample dbSnowflake = innerOrm.getById(SnowflakeExample.class, snowflake.getId());
              UuidExample      dbUuid      = innerOrm.getById(UuidExample.class, uuid.getId());
              IdentityExample  dbIdentity  = innerOrm.getById(IdentityExample.class, identity.getId());

              Assert.assertEquals(0, dbSnowflake.getPrice().compareTo(new BigDecimal("999.00")));
              Assert.assertEquals(0, dbUuid.getPrice().compareTo(new BigDecimal("999.00")));
              Assert.assertEquals(0, dbIdentity.getPrice().compareTo(new BigDecimal("999.00")));

              SnowflakeExample condSnowflake = new SnowflakeExample();
              condSnowflake.setId(dbTemplate.getPkGenerator().nextSnowFakeId());
              condSnowflake.setPrice(new BigDecimal("199.00"));
              innerOrm.updateIgnoreNull(SnowflakeExample.class, condSnowflake);

              dbSnowflake = innerOrm.getById(SnowflakeExample.class, snowflake.getId());

              Assert.assertEquals(snowflake.getName(), dbSnowflake.getName());
              Assert.assertEquals(snowflake.getType(), dbSnowflake.getType());
              Assert.assertEquals(snowflake.getDisable(), dbSnowflake.getDisable());
              Assert.assertEquals(snowflake.getCreated(), dbSnowflake.getCreated());
              Assert.assertEquals(0, snowflake.getPrice().compareTo(dbSnowflake.getPrice()));

              dbUuid.setPrice(new BigDecimal("199.00"));
              innerOrm.update(UuidExample.class, dbUuid, new String[]{"price"});

              dbUuid = innerOrm.getById(UuidExample.class, uuid.getId());
              Assert.assertEquals(0, dbUuid.getPrice().compareTo(new BigDecimal("199.00")));

              for (IdentityExample i : IDENTITIES) {
                i.setPrice(new BigDecimal("1099.00"));
              }
              innerOrm.update(IdentityExample.class, IDENTITIES, new String[]{"name", "price"});

              List<IdentityExample> identities = innerOrm.find(IdentityExample.class, new Sql("select * from tb_identity"));

              for (IdentityExample i : identities) {
                Assert.assertEquals(0, i.getPrice().compareTo(new BigDecimal("1099.00")));
              }

              for (UuidExample u : UUIDS) {
                u.setType(TypeEnum.DRINK);
              }

              innerOrm.update(UuidExample.class, UUIDS);

              List<UuidExample> uuids = innerOrm.find(UuidExample.class, new Sql("select * from tb_uuid"));
              for (UuidExample u : uuids) {
                Assert.assertEquals(TypeEnum.DRINK, u.getType());
              }
            });
  }

  @Test
  public void shouldCreateOrUpdateEntity() {
    initSnowflakes();
    initUuids();
    initIdentities();

    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.truncate(UuidExample.class);
    orm.truncate(IdentityExample.class);

    SnowflakeExample snowflake = SNOW_FLAKES.get(0);
    UuidExample      uuid      = UUIDS.get(0);
    IdentityExample  identity  = IDENTITIES.get(0);

    SnowflakeExample dbSnowflake = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    UuidExample      dbUuid      = orm.getByCond(UuidExample.class, Cond.eq("name", "S1"));
    IdentityExample  dbIdentity  = orm.getByCond(IdentityExample.class, Cond.eq("name", "S1"));

    Assert.assertNull(dbSnowflake);
    Assert.assertNull(dbUuid);
    Assert.assertNull(dbIdentity);

    orm.createOrUpdate(SnowflakeExample.class, snowflake);
    orm.createOrUpdate(UuidExample.class, uuid);
    orm.createOrUpdate(IdentityExample.class, identity);

    dbSnowflake = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    dbUuid = orm.getByCond(UuidExample.class, Cond.eq("name", "S1"));
    dbIdentity = orm.getByCond(IdentityExample.class, Cond.eq("name", "S1"));

    Assert.assertNotNull(dbSnowflake);
    Assert.assertNotNull(dbUuid);
    Assert.assertNotNull(dbIdentity);

    snowflake.setPrice(new BigDecimal("999.99"));
    uuid.setPrice(new BigDecimal("999.99"));
    identity.setPrice(new BigDecimal("999.99"));

    orm.createOrUpdate(SnowflakeExample.class, snowflake);
    orm.createOrUpdate(UuidExample.class, uuid);
    orm.createOrUpdate(IdentityExample.class, identity);

    dbSnowflake = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    dbUuid = orm.getByCond(UuidExample.class, Cond.eq("name", "S1"));
    dbIdentity = orm.getByCond(IdentityExample.class, Cond.eq("name", "S1"));

    Assert.assertEquals(0, dbSnowflake.getPrice().compareTo(new BigDecimal("999.99")));
    Assert.assertEquals(0, dbUuid.getPrice().compareTo(new BigDecimal("999.99")));
    Assert.assertEquals(0, dbIdentity.getPrice().compareTo(new BigDecimal("999.99")));
  }

  @Test
  public void shouldDeleteWithMultipleStrategies() {
    initSnowflakes();
    initUuids();
    initIdentities();
    initLogicDeletes();

    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.truncate(UuidExample.class);
    orm.truncate(IdentityExample.class);
    orm.truncate(LogicDeleteExample.class);

    orm.create(SnowflakeExample.class, SNOW_FLAKES);
    orm.create(UuidExample.class, UUIDS);
    orm.create(IdentityExample.class, IDENTITIES);
    orm.create(LogicDeleteExample.class, LOGIC_DELETES);

    orm.delete(SnowflakeExample.class, SNOW_FLAKES.get(0));
    orm.deleteById(SnowflakeExample.class, SNOW_FLAKES.get(1).getId());
    orm.deleteByCond(SnowflakeExample.class, Cond.eq("name", "S3"));

    orm.delete(UuidExample.class, UUIDS);

    List<Long> ids = IDENTITIES.stream().map(IdentityExample::getId).collect(Collectors.toList());
    orm.deleteByIds(IdentityExample.class, ids);

    orm.deleteByCond(LogicDeleteExample.class, Cond.eq("name", "S1"));

    long c1 = orm.count(new Sql("SELECT * FROM tb_snowflake"));
    long c2 = orm.count(new Sql("SELECT * FROM tb_uuid"));
    long c3 = orm.count(new Sql("SELECT * FROM tb_identity"));
    long c4 = orm.count(new Sql("SELECT * FROM tb_logic_delete"));

    Assert.assertEquals(0, c1);
    Assert.assertEquals(0, c2);
    Assert.assertEquals(0, c3);
    Assert.assertEquals(3, c4);
  }

  @Test
  public void shouldReviveLogicDeletedEntityOnCreateOrUpdate() {
    initLogicDeletes();

    Orm orm = database.createOrm();
    orm.truncate(LogicDeleteExample.class);

    // 创建实体
    LogicDeleteExample entity = LOGIC_DELETES.get(0);
    orm.create(LogicDeleteExample.class, entity);
    Long pk = entity.getId();
    Assert.assertNotNull(pk);

    // 逻辑删除
    orm.delete(LogicDeleteExample.class, entity);

    // 逻辑删除后普通查询查不到
    LogicDeleteExample deleted = orm.getById(LogicDeleteExample.class, pk);
    Assert.assertNull(deleted);

    // createOrUpdate 应"恢复"（UPSERT 将逻辑删除字段置为 0）
    entity.setPrice(new BigDecimal("888.00"));
    orm.createOrUpdate(LogicDeleteExample.class, entity);

    LogicDeleteExample revived = orm.getById(LogicDeleteExample.class, pk);
    Assert.assertNotNull("createOrUpdate 应恢复逻辑删除的记录", revived);
    Assert.assertEquals(0, revived.getPrice().compareTo(new BigDecimal("888.00")));
  }

  @Test
  public void shouldCreateOrUpdateWithAssignedStrategy() {
    AssignedExample entity = new AssignedExample("ASSIGN001");

    Orm orm = database.createOrm();
    orm.truncate(AssignedExample.class);

    // 创建
    int r1 = orm.createOrUpdate(AssignedExample.class, entity);
    Assert.assertEquals(1, r1);

    AssignedExample db1 = orm.getById(AssignedExample.class, "ASSIGN001");
    Assert.assertNotNull(db1);
  }

  @Test
  public void shouldAssignIdOnCreateOrUpdateForIdentityEntity() {
    initIdentities();

    Orm orm = database.createOrm();
    orm.truncate(IdentityExample.class);

    IdentityExample entity = IDENTITIES.get(0);
    // Identity PK 为 null → createOrUpdate 走 Insert 回退，应设置自增 ID 回实体
    Assert.assertNull("IDENTITY 策略初始 PK 应为 null", entity.getId());

    orm.createOrUpdate(IdentityExample.class, entity);

    Assert.assertNotNull("createOrUpdate 后实体的 ID 应被自动回设", entity.getId());
  }
}
