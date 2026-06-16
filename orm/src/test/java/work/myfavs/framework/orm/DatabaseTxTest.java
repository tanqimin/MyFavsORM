package work.myfavs.framework.orm;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.orm.Orm;
import work.myfavs.framework.orm.util.exception.ConnectionException;
import work.myfavs.framework.orm.util.func.ThrowingConsumer;
import work.myfavs.framework.orm.util.func.ThrowingFunction;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DatabaseTxTest {

  @Mock
  private DBTemplate dbTemplate;

  @Mock
  private ConnFactory connFactory;

  private DBConfig    dbConfig;
  private Database    database;
  private Connection  connection;

  @Before
  public void setUp() throws SQLException {
    MockitoAnnotations.openMocks(this);

    dbConfig = new DBConfig();
    dbConfig.setDbType(DbType.H2);

    connection = mock(Connection.class);
    when(connection.getAutoCommit()).thenReturn(false);
    when(connection.isClosed()).thenReturn(false);

    when(dbTemplate.getConnectionFactory()).thenReturn(connFactory);
    when(dbTemplate.getDbConfig()).thenReturn(dbConfig);
    when(connFactory.openConnection()).thenReturn(connection);
    when(connFactory.getCurrentConnection()).thenReturn(connection);

    database = new Database(dbTemplate);
  }

  @Test
  public void tx_ShouldRunCallback_WhenFunctionSucceeds() throws SQLException {
    Runnable callback = mock(Runnable.class);

    String result = database.tx(orm -> "ok", callback);

    assertEquals("ok", result);
    verify(connection).commit();
    verify(callback).run();
    verify(connFactory).closeConnection(connection);
  }

  @Test
  public void tx_ShouldRunCallback_WhenConsumerSucceeds() throws SQLException {
    Runnable callback = mock(Runnable.class);

    database.tx((Orm orm) -> {}, callback);

    verify(connection).commit();
    verify(callback).run();
    verify(connFactory).closeConnection(connection);
  }

  @Test
  public void tx_ShouldReturnFuncResult() {
    String result = database.tx(orm -> "hello");

    assertEquals("hello", result);
  }

  @Test
  public void tx_ShouldCommitAndClose_WhenNoExplicitCallback() throws SQLException {
    database.tx(orm -> "done");

    verify(connection).commit();
    verify(connFactory).closeConnection(connection);
  }

  @Test(expected = ConnectionException.class)
  public void tx_ShouldThrowConnectionException_WhenFuncThrowsSQLException() throws SQLException {
    database.tx((ThrowingFunction<Orm, Object, SQLException>) orm -> {
      throw new SQLException("db error");
    });
  }

  @Test
  public void tx_ShouldRollback_WhenFuncThrowsSQLException() throws SQLException {
    Runnable callback = mock(Runnable.class);
    try {
      database.tx(
          (ThrowingFunction<Orm, Object, SQLException>) orm -> { throw new SQLException("db error"); },
          callback);
    } catch (ConnectionException ignored) {
    }

    verify(connection).rollback();
    verify(connFactory).closeConnection(connection);
  }

  @Test
  public void tx_ShouldRollback_WhenConsumerThrowsSQLException() throws SQLException {
    try {
      database.tx((ThrowingConsumer<Orm, SQLException>) orm -> { throw new SQLException("db error"); });
    } catch (ConnectionException ignored) {
    }

    verify(connection).rollback();
    verify(connFactory).closeConnection(connection);
  }

  @Test
  public void tx_ShouldRollbackAndAddSuppressed_WhenBothFuncAndRollbackThrow() throws SQLException {
    doThrow(new SQLException("rollback failed")).when(connection).rollback();

    try {
      database.tx((ThrowingFunction<Orm, Object, SQLException>) orm -> { throw new SQLException("db error"); });
      fail("Expected ConnectionException");
    } catch (ConnectionException e) {
      assertNotNull(e.getMessage());
    }
  }

  @Test
  public void tx_ShouldCloseConnection_WhenFuncThrows() throws SQLException {
    try {
      database.tx((ThrowingFunction<Orm, Object, SQLException>) orm -> { throw new SQLException("db error"); });
    } catch (ConnectionException ignored) {
    }

    verify(connFactory).closeConnection(connection);
  }

  @Test
  public void tx_ShouldMaintainConnectionDepth_OnSuccess() throws SQLException {
    database.tx(orm -> "ok");

    verify(connFactory, times(2)).openConnection();
    verify(connFactory).closeConnection(connection);
  }
}
