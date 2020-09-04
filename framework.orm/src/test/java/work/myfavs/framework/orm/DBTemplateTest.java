package work.myfavs.framework.orm;

import static org.junit.Assert.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Test;
import work.myfavs.framework.orm.DBTemplate.Builder;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.handler.impls.StringPropertyHandler;

public class DBTemplateTest {


  @Test
  public void builder()
      throws SQLException {

    DBTemplate db1 = new Builder("ds1").dataSource(new HikariDataSource())
                                    .config(config -> config.setDbType(DbType.SQL_SERVER))
                                    .mapping(mapper -> {
                                      mapper.register(String.class, new StringPropertyHandler());
                                    }).build();

    DBTemplate db2 = new Builder("ds2").dataSource(new HikariDataSource())
                                  .config(config -> config.setDbType(DbType.MYSQL))
                                  .mapping(mapper -> {
                                    mapper.register(String.class, new StringPropertyHandler());
                                  }).build();

    Assert.assertEquals(DbType.SQL_SERVER, db1.getDbConfig().getDbType());
    Assert.assertEquals(DbType.MYSQL, db2.getDbConfig().getDbType());
  }

}