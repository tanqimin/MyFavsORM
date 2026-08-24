package work.myfavs.framework.orm.orm.component;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import work.myfavs.framework.orm.*;
import work.myfavs.framework.orm.entity.*;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.orm.dialect.MySqlDialect;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;
import work.myfavs.framework.orm.util.id.PKGenerator;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link OrmUpdater} 的单元测试。
 */
public class OrmUpdaterTest {

  @Mock
  private Database database;
  @Mock
  private DBTemplate dbTemplate;
  @Mock
  private Query query;
  @Mock
  private PKGenerator pkGenerator;

  private OrmSqlBuilder sqlBuilder;
  private OrmExecutor   executor;
  private OrmUpdater    updater;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    DBConfig dbConfig = new DBConfig();
    dbConfig.setDbType(DbType.MYSQL);

    when(database.getDbTemplate()).thenReturn(dbTemplate);
    when(database.getDbConfig()).thenReturn(dbConfig);
    when(database.createQuery(anyString())).thenReturn(query);
    when(database.createQuery(anyString(), anyBoolean())).thenReturn(query);
    when(query.addParameters(any())).thenReturn(query);
    when(query.execute(any(), any())).thenReturn(1);
    when(dbTemplate.getPkGenerator()).thenReturn(pkGenerator);
    when(pkGenerator.nextSnowFakeId()).thenReturn(100L);
    when(pkGenerator.nextUUID()).thenReturn("mock-uuid-1234");

    sqlBuilder = new OrmSqlBuilder(MySqlDialect.INSTANCE);
    executor = new OrmExecutor(database);
    OrmInserter inserter = new OrmInserter(database, dbTemplate, sqlBuilder, executor);
    updater = new OrmUpdater(database, sqlBuilder, executor, inserter);
  }

  @Test
  public void shouldReturnZeroWhenEntityIsNull() {
    int result = updater.update(SnowflakeExample.class, (SnowflakeExample) null);
    assertEquals(0, result);
  }

  @Test
  public void shouldReturnZeroWhenIgnoreNullWithNullEntity() {
    int result = updater.updateIgnoreNull(SnowflakeExample.class, null);
    assertEquals(0, result);
  }

  @Test
  public void shouldUpdateEntity() {
    SnowflakeExample entity = new SnowflakeExample();
    entity.setId(1L);
    entity.setName("Updated");

    int result = updater.update(SnowflakeExample.class, entity);

    assertEquals(1, result);
    verify(database, atLeastOnce()).createQuery(anyString());
  }

  @Test
  public void shouldUpdateEntityWithSpecificColumns() {
    SnowflakeExample entity = new SnowflakeExample();
    entity.setId(1L);
    entity.setName("Updated");

    int result = updater.update(SnowflakeExample.class, entity, new String[]{"name"});

    assertEquals(1, result);
    verify(database, atLeastOnce()).createQuery(anyString());
  }

  @Test
  public void shouldUpdateIgnoreNull() {
    SnowflakeExample entity = new SnowflakeExample();
    entity.setId(1L);
    entity.setName("KeepName");

    int result = updater.updateIgnoreNull(SnowflakeExample.class, entity);

    assertEquals(1, result);
  }

  @Test
  public void shouldReturnZeroForEmptyCollection() {
    int result = updater.update(SnowflakeExample.class, List.of(), new String[]{"name"});
    assertEquals(0, result);
  }

  @Test
  public void shouldUpdateCollection() {
    SnowflakeExample e1 = new SnowflakeExample();
    e1.setId(1L);
    e1.setName("A");
    SnowflakeExample e2 = new SnowflakeExample();
    e2.setId(2L);
    e2.setName("B");

    // MySQL 走 CASE WHEN 批量更新，通过 executor.execute(List<Sql>) 执行
    // mock 的 query.execute() 返回 1，单批结果为 {1}，sum=1
    int result = updater.update(SnowflakeExample.class, List.of(e1, e2));

    assertEquals(1, result);
  }


  @Test
  public void shouldReturnCorrectAffectedRowsForJdbcBatch() {
    DBConfig sqlServerConfig = new DBConfig();
    sqlServerConfig.setDbType(DbType.SQL_SERVER);
    when(database.getDbConfig()).thenReturn(sqlServerConfig);

    when(database.isSqlServer()).thenReturn(true);
    when(query.executeBatch()).thenReturn(new int[3]);

    SnowflakeExample e1 = new SnowflakeExample();
    e1.setId(1L); e1.setName("A");
    SnowflakeExample e2 = new SnowflakeExample();
    e2.setId(2L); e2.setName("B");
    SnowflakeExample e3 = new SnowflakeExample();
    e3.setId(3L); e3.setName("C");

    int result = updater.update(SnowflakeExample.class, List.of(e1, e2, e3));

    assertEquals(3, result);
    verify(query, times(3)).addBatch();
  }
  // ======================== createOrUpdate ========================

  @Test
  public void shouldReturnZeroWhenCreateOrUpdateWithNullEntity() {
    int result = updater.createOrUpdate(SnowflakeExample.class, null);
    assertEquals(0, result);
  }

  @Test
  public void shouldCreateOrUpdateSnowflakeEntity() {
    SnowflakeExample entity = new SnowflakeExample();
    entity.setName("Snowflake1");
    // PK 为 null → 自动生成雪花 ID

    int result = updater.createOrUpdate(SnowflakeExample.class, entity);

    assertEquals(1, result);
    assertNotNull("SNOW_FLAKE 策略应自动生成主键", entity.getId());
    assertEquals(100L, entity.getId().longValue());
    verify(pkGenerator).nextSnowFakeId();
  }

  @Test
  public void shouldCreateOrUpdateUuidEntity() {
    UuidExample entity = new UuidExample();
    entity.setName("Uuid1");
    // PK 为 null → 自动生成 UUID

    int result = updater.createOrUpdate(UuidExample.class, entity);

    assertEquals(1, result);
    assertNotNull("UUID 策略应自动生成主键", entity.getId());
    assertEquals("mock-uuid-1234", entity.getId());
    verify(pkGenerator).nextUUID();
  }

  @Test(expected = InvalidDataAccessException.class)
  public void shouldThrowWhenCreateOrUpdateAssignedWithNullPk() {
    AssignedExample entity = new AssignedExample();
    // PK 为 null，ASSIGNED 策略要求调用方手动赋值

    updater.createOrUpdate(AssignedExample.class, entity);
  }

  @Test
  public void shouldCreateOrUpdateAssignedEntityWithPk() {
    AssignedExample entity = new AssignedExample("A000B000C000D000E000F001");

    int result = updater.createOrUpdate(AssignedExample.class, entity);

    assertEquals(1, result);
  }

  @Test
  public void shouldCreateOrUpdateIdentityWithNullPkFallsBackToInsert() {
    // IDENTITY 策略始终走"先 UPDATE，未命中则 INSERT"
    // mock 中 UPDATE 返回 1（模拟命中），因此不会触发 INSERT
    IdentityExample entity = new IdentityExample();
    entity.setName("Identity1");

    int result = updater.createOrUpdate(IdentityExample.class, entity);

    assertEquals(1, result);
    verify(database, atLeastOnce()).createQuery(anyString(), anyBoolean());
  }

  @Test
  public void shouldCreateOrUpdateIdentityWithPk() {
    // IDENTITY 非空 PK → 先 UPDATE（mock 返回 1 命中了），不会触发 INSERT
    IdentityExample entity = new IdentityExample();
    entity.setId(999L);
    entity.setName("Identity1");

    int result = updater.createOrUpdate(IdentityExample.class, entity);

    assertEquals(1, result);
    verify(database, atLeastOnce()).createQuery(anyString());
  }

  @Test
  public void shouldCreateOrUpdateExistingEntity() {
    SnowflakeExample entity = new SnowflakeExample();
    entity.setId(1L);
    entity.setName("Existing");
    entity.setPrice(new java.math.BigDecimal("100.00"));

    int result = updater.createOrUpdate(SnowflakeExample.class, entity);

    assertEquals(1, result);
    // PK 已存在 → 走 UPSERT 路径，不生成新 PK
    assertEquals(1L, entity.getId().longValue());
  }
}
