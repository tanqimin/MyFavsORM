package work.myfavs.framework.orm;

import org.junit.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.entity.SnowflakeExample;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.entity.test.ISnowflakeTest;
import work.myfavs.framework.orm.meta.TableAlias;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.orm.Orm;
import work.myfavs.framework.orm.util.exception.PaginationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseCoverageTest extends AbstractTest
    implements ISnowflakeTest {

  @Test(expected = PaginationException.class)
  public void shouldThrowExceptionWhenCurrentPageLessThanOne() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.findPageLite(SnowflakeExample.class, Sql.SelectAll().from("tb_snowflake"), true, 0, 2);
  }

  @Test(expected = PaginationException.class)
  public void shouldThrowExceptionWhenPageSizeLessThanOne() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.findPageLite(SnowflakeExample.class, Sql.SelectAll().from("tb_snowflake"), true, 1, 0);
  }

  @Test
  public void shouldThrowExceptionWhenPageSizeExceedsMax() {

    int originalMaxPageSize = dbTemplate.getDbConfig().getMaxPageSize();
    try {
      dbTemplate.getDbConfig().setMaxPageSize(5);

      initSnowflakes();
      Orm orm = database.createOrm();
      orm.truncate(SnowflakeExample.class);
      orm.create(SnowflakeExample.class, SNOW_FLAKES);

      Sql sql = Sql.SelectAll().from("tb_snowflake");
      orm.findPageLite(SnowflakeExample.class, sql, true, 1, 10);
      Assert.fail("Expected PaginationException");
    } catch (PaginationException e) {

    } finally {
      dbTemplate.getDbConfig().setMaxPageSize(originalMaxPageSize);
    }
  }

  @Test
  public void shouldReturnAllDataWhenEnablePageIsFalse() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    Page<SnowflakeExample> page = orm.findPage(
        SnowflakeExample.class, Sql.SelectAll().from("tb_snowflake"), false, 1, 2);

    Assert.assertEquals(3, page.getData().size());
  }

  @Test
  public void shouldReportHasNextWhenExactPageSizeRows() {

    Orm       orm   = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    SnowflakeExample s1 = new SnowflakeExample();
    s1.setCreated(new Date());
    s1.setName("S1");
    s1.setPrice(new BigDecimal("199.00"));
    s1.setType(TypeEnum.FOOD);

    SnowflakeExample s2 = new SnowflakeExample();
    s2.setCreated(new Date());
    s2.setName("S2");
    s2.setPrice(new BigDecimal("299.00"));
    s2.setType(TypeEnum.DRINK);

    orm.create(SnowflakeExample.class, List.of(s1, s2));

    PageLite<SnowflakeExample> page = orm.findPageLite(
        SnowflakeExample.class, Sql.SelectAll().from("tb_snowflake"), true, 1, 2);

    Assert.assertTrue(page.isHasNext());
    Assert.assertEquals(2, page.getData().size());
  }

  @Test
  public void shouldReportHasNextFalseOnLastPage() {

    Orm       orm   = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    SnowflakeExample s1 = new SnowflakeExample();
    s1.setCreated(new Date());
    s1.setName("S1");
    s1.setPrice(new BigDecimal("199.00"));
    s1.setType(TypeEnum.FOOD);

    SnowflakeExample s2 = new SnowflakeExample();
    s2.setCreated(new Date());
    s2.setName("S2");
    s2.setPrice(new BigDecimal("299.00"));
    s2.setType(TypeEnum.DRINK);

    SnowflakeExample s3 = new SnowflakeExample();
    s3.setCreated(new Date());
    s3.setName("S3");
    s3.setPrice(new BigDecimal("399.00"));
    s3.setType(TypeEnum.FOOD);

    orm.create(SnowflakeExample.class, List.of(s1, s2, s3));

    PageLite<SnowflakeExample> page1 = orm.findPageLite(
        SnowflakeExample.class, Sql.SelectAll().from("tb_snowflake"), true, 1, 2);
    Assert.assertTrue(page1.isHasNext());
    Assert.assertEquals(2, page1.getData().size());

    PageLite<SnowflakeExample> page2 = orm.findPageLite(
        SnowflakeExample.class, Sql.SelectAll().from("tb_snowflake"), true, 2, 2);
    Assert.assertFalse(page2.isHasNext());
    Assert.assertEquals(1, page2.getData().size());
  }

  @Test
  public void shouldHandleEmptyPage() {

    Orm       orm   = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    Page<SnowflakeExample> page = orm.findPage(
        SnowflakeExample.class, Sql.SelectAll().from("tb_snowflake"), true, 1, 2);

    Assert.assertNotNull(page);
    Assert.assertTrue(page.getData().isEmpty());
    Assert.assertEquals(0L, page.getTotalRecords());
  }

  @Test
  public void shouldReturnZeroWhenCreatingEmptyCollection() {

    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    int result = orm.create(SnowflakeExample.class, new ArrayList<>());
    Assert.assertEquals(0, result);
  }

  @Test
  public void shouldReturnNullWhenGettingByIdWithNull() {

    Orm          orm   = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    SnowflakeExample result = orm.getById(SnowflakeExample.class, null);
    Assert.assertNull(result);
  }

  @Test
  public void shouldReturnEmptyListWhenFindingByIdsWithEmpty() {

    Orm                orm   = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    List<SnowflakeExample> result = orm.findByIds(SnowflakeExample.class, new ArrayList<>());
    Assert.assertNotNull(result);
    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void shouldExecuteWithConfigConsumer() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    int result = orm.execute(
        "UPDATE tb_snowflake SET disable = ? WHERE name = ?",
        List.of(true, "S1"),
        ps -> ps.setQueryTimeout(5));
    Assert.assertEquals(1, result);

    SnowflakeExample updated = orm.getByCond(SnowflakeExample.class, Cond.eq("name", "S1"));
    Assert.assertTrue(updated.getDisable());
  }

  @Test
  public void shouldExecuteListWithTimeout() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    List<Sql> sqlList = new ArrayList<>();
    sqlList.add(new Sql("UPDATE tb_snowflake SET disable = ? WHERE name = ?", List.of(true, "S1")));
    sqlList.add(new Sql("UPDATE tb_snowflake SET disable = ? WHERE name = ?", List.of(true, "S2")));

    int[] results = orm.execute(sqlList, 3000);
    Assert.assertEquals(2, results.length);
    Assert.assertEquals(1, results[0]);
    Assert.assertEquals(1, results[1]);
  }

  @Test
  public void shouldFindByCriteriaWithLikeOperator() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    SnowflakeExample criteria = new SnowflakeExample();
    criteria.setName("S%");

    List<SnowflakeExample> result = orm.findByCriteria(SnowflakeExample.class, criteria);
    Assert.assertEquals(3, result.size());
  }

  @Test
  public void shouldHandleConfigTextColumn() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);

    SnowflakeExample entity = SNOW_FLAKES.get(0);
    entity.setConfig(null);
    orm.create(SnowflakeExample.class, entity);

    SnowflakeExample dbEntity = orm.getById(SnowflakeExample.class, entity.getId());
    Assert.assertNull(dbEntity.getConfig());

    entity.setConfig("{\"key\": \"value\"}");
    orm.update(SnowflakeExample.class, entity);

    SnowflakeExample updated = orm.getById(SnowflakeExample.class, entity.getId());
    Assert.assertEquals("{\"key\": \"value\"}", updated.getConfig());
  }

  @Test
  public void shouldExecuteQueryWithTableAlias() {

    initSnowflakes();
    Orm orm = database.createOrm();
    orm.truncate(SnowflakeExample.class);
    orm.create(SnowflakeExample.class, SNOW_FLAKES);

    long count = TableAlias.supplier("tb_snowflake", () ->
        orm.countByCond(SnowflakeExample.class, Cond.eq("name", "S1")));
    Assert.assertEquals(1L, count);

    Assert.assertFalse(TableAlias.getOpt().isPresent());
  }

  @Test
  public void shouldRespectMaxPageSize() {

    int original = dbTemplate.getDbConfig().getMaxPageSize();
    try {
      dbTemplate.getDbConfig().setMaxPageSize(5);

      initSnowflakes();
      Orm orm = database.createOrm();
      orm.truncate(SnowflakeExample.class);
      orm.create(SnowflakeExample.class, SNOW_FLAKES);

      Sql sql = Sql.SelectAll().from("tb_snowflake");
      try {
        orm.findPage(SnowflakeExample.class, sql, true, 1, 10);
        Assert.fail("Expected PaginationException");
      } catch (PaginationException e) {
        Assert.assertTrue(e.getMessage().contains("5"));
      }
    } finally {
      dbTemplate.getDbConfig().setMaxPageSize(original);
    }
  }
}
