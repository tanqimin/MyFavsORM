package work.myfavs.framework.orm.orm.component;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.clause.Sql;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * {@link OrmExecutor} 的单元测试。
 * <p>通过 Mockito 模拟 {@link Database} 和 {@link Query}，聚焦执行逻辑本身。</p>
 */
public class OrmExecutorTest {

  @Mock
  private Database database;
  @Mock
  private Query query;

  private OrmExecutor executor;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(database.createQuery(anyString())).thenReturn(query);
    when(query.addParameters(any())).thenReturn(query);
    when(query.execute(any(), any())).thenReturn(1);
    executor = new OrmExecutor(database);
  }

  @Test
  public void shouldExecuteSqlWithStringAndParams() {
    int result = executor.execute("UPDATE tb_user SET name = ?", List.of("test"));

    assertEquals(1, result);
    verify(database).createQuery("UPDATE tb_user SET name = ?");
    verify(query).addParameters(List.of("test"));
    verify(query).execute(any(), isNull());
  }

  @Test
  public void shouldExecuteSqlWithTimeout() {
    executor.execute("DELETE FROM tb_user WHERE id = ?", List.of(1L), 30);

    verify(query).execute(any(), isNull());
  }

  @Test
  public void shouldExecuteSqlObject() {
    Sql sql = new Sql("UPDATE tb_user SET name = ?", List.of("new_name"));
    int result = executor.execute(sql);

    assertEquals(1, result);
    verify(database).createQuery("UPDATE tb_user SET name = ?");
    verify(query).addParameters(List.of("new_name"));
  }

  @Test
  public void shouldExecuteSqlObjectWithTimeout() {
    Sql sql = new Sql("DELETE FROM tb_user");
    executor.execute(sql, 60);

    verify(database).createQuery("DELETE FROM tb_user");
  }

  @Test
  public void shouldExecuteSqlList() {
    List<Sql> sqlList = List.of(
        new Sql("UPDATE t SET x = 1 WHERE id = ?", List.of(1)),
        new Sql("UPDATE t SET x = 2 WHERE id = ?", List.of(2))
    );

    when(query.createQuery(anyString())).thenReturn(query);
    int[] results = executor.execute(sqlList);

    assertEquals(2, results.length);
    verify(database).createQuery("UPDATE t SET x = 1 WHERE id = ?");
    verify(query).createQuery("UPDATE t SET x = 2 WHERE id = ?");
  }

  @Test
  public void shouldExecuteSqlListWithTimeout() {
    List<Sql> sqlList = List.of(new Sql("SELECT 1"));
    when(query.createQuery(anyString())).thenReturn(query);
    executor.execute(sqlList, 30);

    verify(database).createQuery("SELECT 1");
  }

  @Test
  public void shouldReturnEmptyArrayForEmptyList() {
    int[] results = executor.execute(List.of());
    assertEquals(0, results.length);
  }

  @Test
  public void shouldHandleNullParams() {
    executor.execute("SELECT 1", null);
    verify(query).addParameters(null);
  }
}
