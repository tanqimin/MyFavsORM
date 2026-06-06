package work.myfavs.framework.orm.util.convert;

import org.junit.Test;
import org.mockito.Mockito;
import work.myfavs.framework.orm.entity.IdentityExample;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.util.common.Enumerator;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DBConvertTest {

  @Test
  public void toList() {
    int rows = 10;
    //模拟从数据库中查询获取 ResultSet
    try (ResultSet resultSet = IdentityExample.generateResultSet(rows)) {
      List<IdentityExample> entities = DBConvert.toList(IdentityExample.class, resultSet);
      assertEquals(entities.size(), rows);

      for (int i = 0; i < rows; i++) {
        assertEquals(entities.get(i).getId(), Long.valueOf(i + 1));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    try (ResultSet resultSet = IdentityExample.generateResultSet(rows)) {
      List<Record> records = DBConvert.toList(Record.class, resultSet);
      assertEquals(records.size(), rows);

      for (int i = 0; i < rows; i++) {
        assertEquals(records.get(i).getLong("id"), Long.valueOf(i + 1));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    try (ResultSet resultSet = createScalarResultSet(rows)) {
      List<Long> scalars = DBConvert.toList(Long.class, resultSet);
      assertEquals(scalars.size(), rows);
      for (int i = 0; i < rows; i++) {
        assertEquals(scalars.get(i), Long.valueOf(i + 1));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private ResultSet createScalarResultSet(int rows) throws SQLException {
    List<Long> list = new ArrayList<>();
    for (int i = 1; i <= rows; i++) {
      list.add((long) i);
    }
    Enumerator<Long> enumerator = new Enumerator<>(list.iterator());

    ResultSet         rsMock   = Mockito.mock(ResultSet.class);
    ResultSetMetaData rsmdMock = Mockito.mock(ResultSetMetaData.class);

    Mockito.when(rsMock.getMetaData()).thenReturn(rsmdMock);
    Mockito.when(rsmdMock.getColumnCount()).thenReturn(1);
    Mockito.when(rsmdMock.getColumnLabel(1)).thenReturn("id");
    Mockito.when(rsMock.next()).thenAnswer(invocation -> enumerator.next());
    Mockito.when(rsMock.getObject(1)).thenAnswer(invocation -> enumerator.getCurrent());

    return rsMock;
  }
}