package work.myfavs.framework.orm.meta.schema;

import org.junit.Test;
import work.myfavs.framework.orm.entity.Snowflake;
import work.myfavs.framework.orm.meta.dialect.DefaultDialectTest;

public class MetadataTest extends DefaultDialectTest {

  @Test
  public void get() {
    ClassMeta classMeta = Metadata.classMeta(Snowflake.class);
    for (int i = 0; i < 10; i++) {
      //
      classMeta = Metadata.classMeta(Snowflake.class);
    }

    System.out.println(classMeta);
  }
}
