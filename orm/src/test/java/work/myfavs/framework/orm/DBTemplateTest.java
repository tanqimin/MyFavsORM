package work.myfavs.framework.orm;

import org.junit.Assert;
import org.junit.Test;

public class DBTemplateTest extends AbstractTest {

  @Test
  public void getDsName() {
    Assert.assertEquals(DBConfig.DEFAULT_DATASOURCE_NAME, dbTemplate.getDsName());
  }

  @Test
  public void getDataSource() {
    Assert.assertNotNull(dbTemplate.getDataSource());
  }

  @Test
  public void getConnectionFactory() {
    Assert.assertNotNull(dbTemplate.getConnectionFactory());
  }

  @Test
  public void getDbConfig() {
    Assert.assertNotNull(dbTemplate.getDbConfig());
  }

  @Test
  public void getPkGenerator() {
    Assert.assertNotNull(dbTemplate.getPkGenerator());
  }
}
