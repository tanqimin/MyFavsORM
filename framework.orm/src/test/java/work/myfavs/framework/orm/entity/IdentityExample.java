package work.myfavs.framework.orm.entity;

import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.common.Enumerator;
import work.myfavs.framework.orm.util.common.StringUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(value = "tb_identity", strategy = GenerationType.IDENTITY)
public class IdentityExample extends BaseEntity {

  /**
   * ID
   */
  @Column(value = "id")
  @PrimaryKey
  private Long id = null;

  public Long getId() {

    return id;
  }

  public void setId(Long id) {

    this.id = id;
  }

  public static ResultSet generateResultSet(int rows) {

    List<IdentityExample> list = generateList(rows);

    Enumerator<IdentityExample> enumerator = new Enumerator<>(list.iterator());

    ResultSet         rsMock   = Mockito.mock(ResultSet.class);
    ResultSetMetaData rsmdMock = Mockito.mock(ResultSetMetaData.class);

    try {

      Mockito.when(rsMock.getMetaData()).thenReturn(rsmdMock);
      Mockito.when(rsmdMock.getColumnCount()).thenReturn(7);
      Mockito.when(rsmdMock.getColumnLabel(1)).thenReturn("id");
      Mockito.when(rsmdMock.getColumnLabel(2)).thenReturn("name");
      Mockito.when(rsmdMock.getColumnLabel(3)).thenReturn("price");
      Mockito.when(rsmdMock.getColumnLabel(4)).thenReturn("type");
      Mockito.when(rsmdMock.getColumnLabel(5)).thenReturn("created");
      Mockito.when(rsmdMock.getColumnLabel(6)).thenReturn("disable");
      Mockito.when(rsmdMock.getColumnLabel(7)).thenReturn("config");

      Mockito.when(rsMock.next()).thenAnswer(invocation -> enumerator.next());
      Mockito.when(rsMock.getObject(1)).thenAnswer(invocation -> enumerator.getCurrent().getId());
      Mockito.when(rsMock.getObject(2)).thenAnswer(invocation -> enumerator.getCurrent().getName());
      Mockito.when(rsMock.getObject(3)).thenAnswer(invocation -> enumerator.getCurrent().getPrice());
      Mockito.when(rsMock.getObject(4)).thenAnswer(invocation -> enumerator.getCurrent().getType().name());
      Mockito.when(rsMock.getObject(5)).thenAnswer(invocation -> enumerator.getCurrent().getCreated());
      Mockito.when(rsMock.getObject(6)).thenAnswer(invocation -> enumerator.getCurrent().getDisable());
      Mockito.when(rsMock.getObject(7)).thenAnswer(invocation -> enumerator.getCurrent().getConfig());

      return rsMock;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public static List<IdentityExample> generateList(int rows) {
    List<IdentityExample> results = new ArrayList<>(rows);

    int flowLength = StringUtil.length(rows);
    for (int i = 1; i <= rows; i++) {
      String   flow     = StringUtil.leftPad(StringUtil.toStr(i), "0", flowLength);
      TypeEnum typeEnum = i % 2 == 0 ? TypeEnum.DRINK : TypeEnum.FOOD;

      IdentityExample row = new IdentityExample();
      row.setId((long) i);
      row.setName("S".concat(flow));
      row.setPrice(new BigDecimal(i));
      row.setType(typeEnum);
      row.setCreated(new Date());
      row.setDisable(i % 3 == 0);
      row.setConfig("CONFIG_".concat(flow));
      results.add(row);
    }
    return results;
  }
}
