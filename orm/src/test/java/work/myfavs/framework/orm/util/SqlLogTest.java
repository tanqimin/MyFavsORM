package work.myfavs.framework.orm.util;

import java.util.List;
import org.junit.Test;
import work.myfavs.framework.orm.meta.BatchParameters;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.SqlLog;

public class SqlLogTest {

  private static final String TEST_SQL = "SELECT * FROM tb_user WHERE id = ?";

  @Test
  public void shouldLogSqlWhenShowSqlIsTrue() {

    SqlLog sqlLog = new SqlLog(true, false);
    sqlLog.showSql(TEST_SQL);
  }

  @Test
  public void shouldNotLogSqlWhenShowSqlIsFalse() {

    SqlLog sqlLog = new SqlLog(false, false);
    sqlLog.showSql(TEST_SQL);
  }

  @Test
  public void shouldLogAffectedRowsForZero() {

    SqlLog sqlLog = new SqlLog(false, true);
    sqlLog.showAffectedRows(0);
  }

  @Test
  public void shouldLogAffectedRowsForOne() {

    SqlLog sqlLog = new SqlLog(false, true);
    sqlLog.showAffectedRows(1);
  }

  @Test
  public void shouldLogAffectedRowsForMany() {

    SqlLog sqlLog = new SqlLog(false, true);
    sqlLog.showAffectedRows(100);
  }

  @Test
  public void shouldLogResultForRecordList() {

    SqlLog sqlLog = new SqlLog(false, true);
    List<Record> records = List.of(
        Record.create().set("name", "Alice").set("age", 30),
        Record.create().set("name", "Bob").set("age", 25)
    );
    sqlLog.showResult(Record.class, records);
  }

  @Test
  public void shouldLogResultForScalarList() {

    SqlLog sqlLog = new SqlLog(false, true);
    sqlLog.showResult(Long.class, List.of(1L, 2L, 3L));
  }

  @Test
  public void shouldLogResultForEntityList() {

    SqlLog sqlLog = new SqlLog(false, true);
    sqlLog.showResult(Person.class, List.of(new Person("Alice", 30)));
  }

  @Test
  public void shouldHandleEmptyResultList() {

    SqlLog sqlLog = new SqlLog(false, true);
    sqlLog.showResult(Record.class, List.of());
    sqlLog.showResult(Long.class, List.of());
    sqlLog.showResult(Person.class, List.of());
  }

  @Test
  public void shouldShowParamsForSingleBatch() {

    SqlLog       sqlLog   = new SqlLog(true, false);
    BatchParameters bp     = new BatchParameters();
    bp.getCurrentBatchParameters().addParameter("Alice");
    bp.getCurrentBatchParameters().addParameter(30);
    sqlLog.showParams(bp);
  }

  @Test
  public void shouldShowParamsForMultiBatch() {

    SqlLog       sqlLog   = new SqlLog(true, false);
    BatchParameters bp     = new BatchParameters();
    bp.getCurrentBatchParameters().addParameter("Alice");
    bp.addBatch();
    bp.getCurrentBatchParameters().addParameter("Bob");
    sqlLog.showParams(bp);
  }

  @Test
  public void shouldHandleNullParams() {

    SqlLog sqlLog = new SqlLog(true, false);
    sqlLog.showParams(null);
  }

  @Test
  public void shouldHandleEmptyParams() {

    SqlLog         sqlLog = new SqlLog(true, false);
    BatchParameters bp    = new BatchParameters();
    sqlLog.showParams(bp);
  }

  @Test
  public void shouldLogFormattedResultMessage() {

    SqlLog sqlLog = new SqlLog(false, true);
    sqlLog.showResult("语句执行成功, 耗时: {} ms", 42);
  }

  static class Person {

    private String name;
    private int    age;

    public Person(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public int getAge() {
      return age;
    }

    public void setAge(int age) {
      this.age = age;
    }
  }
}
