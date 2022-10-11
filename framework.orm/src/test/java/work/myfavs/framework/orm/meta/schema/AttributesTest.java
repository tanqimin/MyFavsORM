package work.myfavs.framework.orm.meta.schema;

import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.entity.Snowflake;

public class AttributesTest {

  Attributes attributes;

  @Before
  public void setUp() throws Exception {
    attributes = Metadata.get(Snowflake.class).getQueryAttributes();
  }

  @Test
  public void get() {}

  @Test
  public void columns() {
    for (String column : attributes.columns()) {
      System.out.println(column);
    }
  }

  @Test
  public void put() {}

  @Test
  public void forEach() {}

  @Test
  public void computeIfAbsent() {}
}
