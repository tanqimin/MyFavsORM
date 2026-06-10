package work.myfavs.framework.orm.orm.component;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.orm.dialect.MySqlDialect;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link OrmSelector} 的单元测试。
 */
public class OrmSelectorTest {

  @Mock
  private Database database;
  @Mock
  private Query query;

  private OrmSqlBuilder sqlBuilder;
  private OrmSelector selector;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    when(database.createQuery(anyString())).thenReturn(query);
    when(query.addParameters(any())).thenReturn(query);
    sqlBuilder = new OrmSqlBuilder(MySqlDialect.INSTANCE);
    selector = new OrmSelector(database, sqlBuilder);
  }

  @Test
  public void shouldFindRecordsBySql() {
    Record record = new Record();
    record.set("id", 1L);
    when(query.find(Record.class)).thenReturn(List.of(record));

    List<Record> results = selector.find(Record.class, "SELECT * FROM tb_user", List.of());

    assertEquals(1, results.size());
    verify(database).createQuery("SELECT * FROM tb_user");
  }

  @Test
  public void shouldFindRecordsBySqlObject() {
    Record record = new Record();
    record.set("id", 1L);
    when(query.find(Record.class)).thenReturn(List.of(record));

    List<Record> results = selector.find(Record.class, new Sql("SELECT * FROM tb_user"));

    assertEquals(1, results.size());
  }

  @Test
  public void shouldGetSingleResult() {
    Record record = new Record();
    record.set("name", "test");
    when(query.find(Record.class)).thenReturn(List.of(record));

    Record result = selector.get(Record.class, "SELECT * FROM tb_user WHERE name = ?", List.of("test"));

    assertNotNull(result);
    assertEquals("test", result.get("name"));
  }

  @Test
  public void shouldReturnNullWhenNoResult() {
    when(query.find(Record.class)).thenReturn(List.of());

    Record result = selector.get(Record.class, "SELECT * FROM tb_user WHERE 1=0", List.of());

    assertNull(result);
  }

  @Test
  public void shouldFindMap() {
    MapBean bean = new MapBean("key1");
    when(query.find(MapBean.class)).thenReturn(List.of(bean));

    Map<Object, MapBean> map = selector.findMap(MapBean.class, "name",
        "SELECT * FROM tb_map", List.of());

    assertEquals(1, map.size());
    assertEquals(bean, map.get("key1"));
  }

  @Test
  public void shouldCountRecords() {
    when(query.find(Number.class)).thenReturn(List.of(5L));

    long count = selector.count("SELECT * FROM tb_user", List.of());

    assertEquals(5L, count);
  }

  @Test
  public void shouldCheckExists() {
    when(query.find(Number.class)).thenReturn(List.of(1L));

    boolean exists = selector.exists("SELECT * FROM tb_user WHERE id = ?", List.of(1L));

    assertTrue(exists);
  }

  @Test
  public void shouldCheckNotExists() {
    when(query.find(Number.class)).thenReturn(List.of(0L));

    boolean exists = selector.exists("SELECT * FROM tb_user WHERE 1=0", List.of());

    assertFalse(exists);
  }

  @Test
  public void shouldReturnNullWhenGetByIdWithNull() {
    assertNull(selector.getById(String.class, null));
  }

  @Test
  public void shouldReturnFalseWhenExistsWithNullEntity() {
    assertFalse(selector.exists(String.class, null));
  }

  // 用于 findMap 测试的内部 POJO 类
  public static class MapBean {
    public String name;
    public MapBean(String name) { this.name = name; }
  }
}
