package work.myfavs.framework.orm.meta.handler.impls;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.junit.Test;

public class BigDecimalPropertyHandlerTest {

  @Test
  public void convert() {
    final Type type = ((ParameterizedType) BigDecimalPropertyHandler.class
        .getGenericSuperclass()).getActualTypeArguments()[0];

    final Class<?> clazz = (Class<?>) type;
    System.out.println(clazz.getName());
  }

  @Test
  public void addParameter() {
  }
}