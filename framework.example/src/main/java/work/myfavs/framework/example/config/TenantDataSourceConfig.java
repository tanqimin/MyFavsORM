package work.myfavs.framework.example.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import work.myfavs.framework.example.domain.entity.Tenant;
import work.myfavs.framework.example.util.tenant.DynamicDataSource;
import work.myfavs.framework.orm.*;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.handler.impls.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

@Configuration
public class TenantDataSourceConfig {
  private static final Logger logger = LoggerFactory.getLogger(TenantDataSourceConfig.class);

  @Bean(name = "primaryDataSource")
  @ConfigurationProperties("spring.datasource.primary")
  public DataSource dataSource() {

    DruidDataSource datasource = DruidDataSourceBuilder.create().build();
    datasource.setDefaultAutoCommit(false);
    return datasource;
  }

  @Primary
  @Bean(name = "dataSource")
  public DynamicDataSource dynamicDataSource(
      @Qualifier("primaryDataSource") DataSource dataSource) {
    logger.info("=====初始化动态数据源=====");

    DynamicDataSource dynamicDataSource = new DynamicDataSource();
    // 默认数据源
    dynamicDataSource.setDefaultTargetDataSource(dataSource);

    DBTemplate dbTemplate = this.buildDbTemplate(dataSource, JdbcConnFactory.class);
    DB db = DB.conn(dbTemplate);
    List<Tenant> tenants = db.find(Tenant.class, new Sql("SELECT * FROM tenant"));

    Map<Object, Object> customDataSources = new HashMap<>();

    for (Tenant tenant : tenants) {
      DruidDataSource ds = new DruidDataSource();
      ds.setDriverClassName(tenant.getJdbcClass());
      ds.setUrl(tenant.getJdbcUrl());
      ds.setUsername(tenant.getJdbcUser());
      ds.setPassword(tenant.getJdbcPassword());
      customDataSources.put(tenant.getTenant(), ds);
      logger.info("已加载租户库数据源" + tenant.getTenant());
    }

    dynamicDataSource.setTargetDataSources(customDataSources);
    return dynamicDataSource;
  }

  @Bean
  @Primary
  public DataSourceTransactionManager dataSourceTransactionManager(
      @Qualifier("dataSource") DataSource dataSource) {

    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public DBTemplate dbTemplate(@Qualifier("dataSource") DataSource dataSource) {
    return this.buildDbTemplate(dataSource, SpringConnFactory.class);
  }

  private DBTemplate buildDbTemplate(
      DataSource dataSource, Class<? extends ConnFactory> connectionFactory) {

    return new DBTemplate.Builder()
        .dataSource(dataSource)
        .connectionFactory(connectionFactory)
        .config(
            config ->
                config
                    .setDbType(DbType.MYSQL)
                    .setBatchSize(200)
                    .setFetchSize(100)
                    .setQueryTimeout(120)
                    .setDataCenterId(1L)
                    .setWorkerId(1L)
                    .setPageDataField("list")
                    .setPageCurrentField("pageNumber")
                    .setPageSizeField("pageSize")
                    .setPageTotalPageField("totalPage")
                    .setPageTotalRecordField("totalRow")
                    .setPageHasNextField("next"))
        .mapping(
            mapper ->
                mapper
                    .register(String.class, new StringPropertyHandler())
                    .register(BigDecimal.class, new BigDecimalPropertyHandler())
                    .register(Long.class, new LongPropertyHandler())
                    .register(long.class, new LongPropertyHandler(true))
                    .register(Boolean.class, new BooleanPropertyHandler())
                    .register(int.class, new IntegerPropertyHandler(true))
                    .register(Date.class, new DatePropertyHandler()))
        .build();
  }
}
