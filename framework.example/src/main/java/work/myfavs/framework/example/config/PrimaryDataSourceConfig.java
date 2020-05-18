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
import work.myfavs.framework.orm.SpringConnFactory;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.handler.impls.*;

@Configuration
public class PrimaryDataSourceConfig {

  @Bean(name = "primaryDataSource")
  @Primary
  @ConfigurationProperties("spring.datasource.primary")
  public DataSource primaryDataSource() {

    DruidDataSource datasource = DruidDataSourceBuilder.create()
                                                       .build();
    datasource.setDefaultAutoCommit(false);
    return datasource;
  }

  @Bean(name = "primaryTransactionManager")
  @Primary
  public DataSourceTransactionManager primaryTransactionManager() {

    return new DataSourceTransactionManager(primaryDataSource());
  }

  @Bean(name = "primaryDBTemplate")
  public DBTemplate dbTemplate() {

    return new DBTemplate.Builder().dataSource(primaryDataSource())
                                   .connectionFactory(SpringConnFactory.class)
                                   .config(config -> {
                                     config.setDbType(DbType.MYSQL)
                                           .setBatchSize(200)
                                           .setFetchSize(100)
                                           .setQueryTimeout(120)
                                           .setDataCenterId(1L)
                                           .setWorkerId(1L);
                                   })
                                   .mapping(mapper -> {
                                     mapper.register(String.class, new StringPropertyHandler())
                                           .register(BigDecimal.class, new BigDecimalPropertyHandler())
                                           .register(Long.class, new LongPropertyHandler())
                                           .register(long.class, new LongPropertyHandler(true))
                                           .register(Boolean.class, new BooleanPropertyHandler())
                                           .register(int.class, new IntegerPropertyHandler(true))
                                           .register(Date.class, new DatePropertyHandler());
                                   })
                                   .build();
  }

}
