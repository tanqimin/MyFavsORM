package work.myfavs.framework.orm.util.exception;

import java.sql.SQLException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class DBExceptionTest {

  @Test
  public void dataRetrievalExceptionWithCauseAndMessage() {
    SQLException cause = new SQLException("连接超时");
    DataRetrievalException ex = new DataRetrievalException(cause, "查询失败: %s", "users");
    assertEquals("查询失败: users", ex.getMessage());
    assertSame(cause, ex.getCause());
  }

  @Test
  public void connectionExceptionWithCauseAndMessage() {
    SQLException cause = new SQLException("连接超时");
    ConnectionException ex = new ConnectionException(cause, "连接失败: %s", "db");
    assertEquals("连接失败: db", ex.getMessage());
    assertSame(cause, ex.getCause());
  }

  @Test
  public void invalidDataAccessExceptionWithCauseAndMessage() {
    SQLException cause = new SQLException("SQL 注入");
    InvalidDataAccessException ex = new InvalidDataAccessException(cause, "参数非法: %s", "id");
    assertEquals("参数非法: id", ex.getMessage());
    assertSame(cause, ex.getCause());
  }

  @Test
  public void paginationExceptionWithCauseAndMessage() {
    SQLException cause = new SQLException("分页");
    PaginationException ex = new PaginationException(cause, "分页失败: %s", "1");
    assertEquals("分页失败: 1", ex.getMessage());
    assertSame(cause, ex.getCause());
  }
}
