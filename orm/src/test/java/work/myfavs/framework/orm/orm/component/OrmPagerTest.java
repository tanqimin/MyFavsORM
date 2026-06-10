package work.myfavs.framework.orm.orm.component;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import work.myfavs.framework.orm.DBConfig;
import work.myfavs.framework.orm.Database;
import work.myfavs.framework.orm.Query;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.pagination.IPageable;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.orm.dialect.MySqlDialect;
import work.myfavs.framework.orm.util.exception.PaginationException;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link OrmPager} 的单元测试。
 */
public class OrmPagerTest {

  @Mock
  private Database database;
  @Mock
  private Query query;

  private OrmSqlBuilder sqlBuilder;
  private OrmSelector selector;
  private DBConfig dbConfig;
  private MySqlDialect dialect;
  private OrmPager pager;

  @Before
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    dbConfig = new DBConfig();
    dbConfig.setDbType("mysql");
    dbConfig.setMaxPageSize(1000);

    sqlBuilder = new OrmSqlBuilder(MySqlDialect.INSTANCE);
    selector = new OrmSelector(database, sqlBuilder);
    dialect = MySqlDialect.INSTANCE;

    when(database.createQuery(anyString())).thenReturn(query);
    when(query.addParameters(any())).thenReturn(query);
    when(query.find(any())).thenAnswer(invocation -> {
      Class<?> clazz = invocation.getArgument(0);
      if (Number.class.isAssignableFrom(clazz)) {
        return List.of(3L);
      }
      return List.of(createRecord(1L), createRecord(2L), createRecord(3L));
    });

    pager = new OrmPager(selector, sqlBuilder, dbConfig, dialect);
  }

  private Record createRecord(Long id) {
    Record r = new Record();
    r.set("id", id);
    return r;
  }

  @Test
  public void shouldFindPageLite() {
    PageLite<Record> page = pager.findPageLite(
        Record.class,
        "SELECT * FROM tb_user ORDER BY id",
        List.of(),
        true,
        1,
        2);

    assertEquals(3, page.getData().size());
    assertEquals(1, page.getCurrentPage());
    assertEquals(2, page.getPageSize());
  }

  @Test
  public void shouldFindPageWithTotalCount() {
    Page<Record> page = pager.findPage(
        Record.class,
        "SELECT * FROM tb_user ORDER BY id",
        List.of(),
        true,
        1,
        2);

    assertEquals(3, page.getData().size());
    assertEquals(3L, page.getTotalRecords());
    assertEquals(2L, page.getTotalPages());
  }

  @Test
  public void shouldFindPageWithoutPagination() {
    Page<Record> page = pager.findPage(
        Record.class,
        "SELECT * FROM tb_user",
        List.of(),
        false,
        1,
        Integer.MAX_VALUE);

    assertEquals(3, page.getData().size());
    assertEquals(3L, page.getTotalRecords());
  }

  @Test
  public void shouldFindPageWithIPageable() {
    IPageable pageable = new IPageable() {
      @Override
      public boolean getEnablePage() { return true; }

      @Override
      public int getCurrentPage() { return 1; }

      @Override
      public int getPageSize() { return 2; }
    };

    PageLite<Record> pageLite = pager.findPageLite(
        Record.class,
        "SELECT * FROM tb_user ORDER BY id",
        List.of(),
        pageable);

    assertEquals(3, pageLite.getData().size());
  }

  @Test(expected = PaginationException.class)
  public void shouldThrowWhenCurrentPageLessThanOne() {
    pager.findPageLite(Record.class, "SELECT 1", List.of(), true, 0, 10);
  }

  @Test(expected = PaginationException.class)
  public void shouldThrowWhenPageSizeLessThanOne() {
    pager.findPageLite(Record.class, "SELECT 1", List.of(), true, 1, 0);
  }

  @Test(expected = PaginationException.class)
  public void shouldThrowWhenPageSizeExceedsMax() {
    dbConfig.setMaxPageSize(100);
    pager.findPageLite(Record.class, "SELECT 1", List.of(), true, 1, 200);
  }

  @Test
  public void shouldFindTopRecords() {
    List<Record> top = pager.findTop(Record.class, 1, "SELECT * FROM tb_user", List.of());
    assertEquals(3, top.size());
  }
}
