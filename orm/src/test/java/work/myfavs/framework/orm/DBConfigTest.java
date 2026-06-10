package work.myfavs.framework.orm;

import org.junit.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.meta.DbType;

import java.sql.Connection;

public class DBConfigTest {

  @Test
  public void shouldCreateDBConfigWithAllProperties() {
    DBConfig dbConfig = new DBConfig()
        .setDbType(DbType.MYSQL)
        .setDefaultIsolation(Connection.TRANSACTION_READ_UNCOMMITTED)
        .setShowSql(true)
        .setShowResult(true)
        .setBatchSize(100)
        .setFetchSize(100)
        .setMaxPageSize(1000)
        .setDataCenterId(1)
        .setWorkerId(1)
        .setPageCurrentField("current")
        .setPageDataField("data")
        .setPageSizeField("size")
        .setPageHasNextField("hasNext")
        .setPageTotalPageField("totalPages")
        .setPageTotalRecordField("totalRecords");

    Assert.assertEquals(dbConfig.getDefaultIsolation(), Connection.TRANSACTION_READ_UNCOMMITTED);
    Assert.assertEquals(DbType.MYSQL, dbConfig.getDbType());
    Assert.assertTrue(dbConfig.getShowSql());
    Assert.assertTrue(dbConfig.getShowResult());
    Assert.assertEquals(100, dbConfig.getBatchSize());
    Assert.assertEquals(100, dbConfig.getFetchSize());
    Assert.assertEquals(1000, dbConfig.getMaxPageSize());
    Assert.assertEquals(1, dbConfig.getDataCenterId());
    Assert.assertEquals(1, dbConfig.getWorkerId());
    Assert.assertEquals("current", dbConfig.getPageCurrentField());
    Assert.assertEquals("data", dbConfig.getPageDataField());
    Assert.assertEquals("size", dbConfig.getPageSizeField());
    Assert.assertEquals("hasNext", dbConfig.getPageHasNextField());
    Assert.assertEquals("totalPages", dbConfig.getPageTotalPageField());
    Assert.assertEquals("totalRecords", dbConfig.getPageTotalRecordField());
  }
}
