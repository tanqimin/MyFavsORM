package work.myfavs.framework.orm.orm.component;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import work.myfavs.framework.orm.*;
import work.myfavs.framework.orm.entity.LogicDeleteExample;
import work.myfavs.framework.orm.entity.SnowflakeExample;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.orm.dialect.MySqlDialect;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.id.PKGenerator;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link OrmDeleter} 的单元测试。
 */
public class OrmDeleterTest {

  @Mock
  private Database database;
  @Mock
  private DBTemplate dbTemplate;
  @Mock
  private Query query;

  private OrmSqlBuilder sqlBuilder;
  private OrmExecutor executor;
  private OrmDeleter deleter;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    DBConfig dbConfig = new DBConfig();
    dbConfig.setDbType(DbType.MYSQL);

    when(database.getDbTemplate()).thenReturn(dbTemplate);
    when(database.getDbConfig()).thenReturn(dbConfig);
    when(database.createQuery(anyString())).thenReturn(query);
    when(query.addParameters(any())).thenReturn(query);
    when(query.execute(any(), any())).thenReturn(1);
    when(dbTemplate.getPkGenerator()).thenReturn(mock(PKGenerator.class));

    sqlBuilder = new OrmSqlBuilder(MySqlDialect.INSTANCE);
    executor = new OrmExecutor(database);
    deleter = new OrmDeleter(database, sqlBuilder, executor);
  }

  @Test
  public void shouldReturnZeroWhenEntityIsNull() {
    int result = deleter.delete(SnowflakeExample.class, (SnowflakeExample) null);
    assertEquals(0, result);
  }

  @Test
  public void shouldReturnZeroWhenCollectionIsEmpty() {
    int result = deleter.delete(SnowflakeExample.class, List.of());
    assertEquals(0, result);
  }

  @Test
  public void shouldReturnZeroWhenIdIsNull() {
    int result = deleter.deleteById(SnowflakeExample.class, null);
    assertEquals(0, result);
  }

  @Test
  public void shouldReturnZeroWhenCondIsNull() {
    int result = deleter.deleteByCond(SnowflakeExample.class, null);
    assertEquals(0, result);
  }

  @Test
  public void shouldDeleteEntityById() {
    int result = deleter.deleteById(SnowflakeExample.class, 1L);
    assertEquals(1, result);
  }

  @Test
  public void shouldDeleteEntityByEntity() {
    SnowflakeExample entity = new SnowflakeExample();
    entity.setId(42L);

    int result = deleter.delete(SnowflakeExample.class, entity);

    assertEquals(1, result);
  }

  @Test
  public void shouldDeleteByIds() {
    int result = deleter.deleteByIds(SnowflakeExample.class, List.of(1L, 2L, 3L));
    assertEquals(1, result);
  }

  @Test
  public void shouldDeleteByCond() {
    int result = deleter.deleteByCond(SnowflakeExample.class,
        work.myfavs.framework.orm.meta.clause.Cond.eq("name", "test"));
    assertEquals(1, result);
  }

  @Test
  public void shouldLogicDelete() {
    LogicDeleteExample entity = new LogicDeleteExample();
    entity.setId(1L);

    deleter.delete(LogicDeleteExample.class, entity);

    // 逻辑删除走 update 路径（在 executor.execute 中）
    verify(database, atLeastOnce()).createQuery(anyString());
  }

  @Test
  public void shouldTruncateTable() {
    when(query.execute(any(), any())).thenReturn(0);

    deleter.truncate(SnowflakeExample.class);

    // 应该触发一次 SQL 执行
    verify(database, atLeastOnce()).createQuery(anyString());
  }
}
