package work.myfavs.framework.example.quickstart;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.List;
import javax.sql.DataSource;
import work.myfavs.framework.orm.DB;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Sql;

public class MyDao {

  private static final DBTemplate DBTemplate;

  static {
    DBTemplate = new DBTemplate.Builder().dataSource(getDataSource()).build();
  }

  public List<Record> findRecord() {

    Sql sql = new Sql("SELECT * FROM tb_snowfake");
    return DB.conn().find(sql);
  }

  private static DataSource getDataSource() {

    HikariConfig configuration = new HikariConfig();
    configuration.setDriverClassName("com.mysql.jdbc.Driver");
    configuration.setJdbcUrl(
        "jdbc:mysql://127.0.0.1:3306/myfavs_test?useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8");
    configuration.setUsername("root");
    configuration.setPassword("root");
    configuration.setAutoCommit(false);
    return new HikariDataSource(configuration);
  }
}
