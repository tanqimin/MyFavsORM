package work.myfavs.framework.orm;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.meta.DbType;

import java.sql.Connection;

public class DBConfigTest extends TestCase {

  public void setUp() throws Exception {
    super.setUp();
  }

  public void testCreate(){
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

  }
}