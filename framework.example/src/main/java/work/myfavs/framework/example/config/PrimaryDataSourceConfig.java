package work.myfavs.framework.example.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import work.myfavs.framework.example.util.exts.Config;
import work.myfavs.framework.example.util.exts.ConfigPropertyHandler;
import work.myfavs.framework.orm.DBTemplate;

@Configuration
public class PrimaryDataSourceConfig {

  @Bean(name = "primaryDataSource", destroyMethod = "close")
  @Primary
  @ConfigurationProperties("spring.datasource.primary")
  public DruidDataSource primaryDataSource() {

    return DruidDataSourceBuilder.create().build();
  }

  @Bean(name = "primaryTransactionManager")
  @Primary
  public DataSourceTransactionManager primaryTransactionManager() {

    return new DataSourceTransactionManager(primaryDataSource());
  }

  @Bean(name = "primaryDBTemplate", destroyMethod = "close")
  public DBTemplate dbTemplate() {

    DruidDataSource druidDataSource = primaryDataSource();
    DBTemplate      dbTemplate      = new DBTemplate(druidDataSource);
    dbTemplate.setDbType(druidDataSource.getDbType());
    dbTemplate.setBatchSize(50);
    dbTemplate.setFetchSize(100);
    dbTemplate.setQueryTimeout(30);
    dbTemplate.setDataCenterId(1);
    dbTemplate.setWorkerId(1);
    dbTemplate.registerPropertyHandler(Config.class, new ConfigPropertyHandler());
    return dbTemplate;
  }

}
