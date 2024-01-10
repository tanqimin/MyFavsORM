package work.myfavs.framework.orm.meta.handler.impls;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalPropertyHandlerTest {

  BigDecimal                decimal         = new BigDecimal("1.2345");
  BigDecimalPropertyHandler propertyHandler = new BigDecimalPropertyHandler();

  @Test
  public void convert() {
    try (ResultSet rsMock = Mockito.mock(ResultSet.class)) {
      Mockito.when(rsMock.next()).
             thenReturn(true).
             thenReturn(false);

      Mockito.when(rsMock.getObject(1)).thenReturn(1.2345);

      if (rsMock.next()) {
        BigDecimal result = propertyHandler.convert(rsMock, 1, BigDecimal.class);
        Assert.assertEquals(result, decimal);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void addParameter() {
    try (PreparedStatement psMock = Mockito.mock(PreparedStatement.class)) {

      propertyHandler.addParameter(psMock, 1, decimal);
      Mockito.verify(psMock).setBigDecimal(1, decimal);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
