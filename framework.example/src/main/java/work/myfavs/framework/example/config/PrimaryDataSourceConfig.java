package work.myfavs.framework.example.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import java.math.BigDecimal;
import java.util.Date;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import work.myfavs.framework.orm.DBTemplate;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.handler.impls.*;

@Configuration
public class PrimaryDataSourceConfig {

  @Bean(name = "primaryDataSource")
  @Primary
  @ConfigurationProperties("spring.datasource.primary")
  public DataSource primaryDataSource() {

    DruidDataSource datasource = DruidDataSourceBuilder.create().build();
    datasource.setDefaultAutoCommit(false);
    return datasource;
  }

  @Bean(name = "primaryTransactionManager")
  @Primary
  public DataSourceTransactionManager primaryTransactionManager() {

    return new DataSourceTransactionManager(primaryDataSource());
  }

  @Bean(name = "primaryDBTemplate", destroyMethod = "close")
  public DBTemplate dbTemplate() {

    return DBTemplate.build(primaryDataSource())
                     .registerPropertyHandler(String.class, new StringPropertyHandler())
                     .registerPropertyHandler(BigDecimal.class, new BigDecimalPropertyHandler())
                     .registerPropertyHandler(Long.class, new LongPropertyHandler())
                     .registerPropertyHandler(Boolean.class, new BooleanPropertyHandler())
                     .registerPropertyHandler(Date.class, new DatePropertyHandler())
                     .setDbType(DbType.MYSQL)
//                     .setShowSql(true)
//                     .setShowResult(true)
                     .setBatchSize(200)
                     .setFetchSize(100)
//                     .setMaxPageSize(100)
                     .setQueryTimeout(120);
  }

}
