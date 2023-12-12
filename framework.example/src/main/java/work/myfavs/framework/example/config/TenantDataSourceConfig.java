package work.myfavs.framework.example.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

import java.math.BigDecimal;
import java.util.*;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import work.myfavs.framework.example.domain.entity.Tenant;
import work.myfavs.framework.example.util.tenant.DynamicDataSource;
import work.myfavs.framework.orm.*;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.handler.impls.*;

@Configuration
public class TenantDataSourceConfig {
  private static final Logger logger = LoggerFactory.getLogger(TenantDataSourceConfig.class);

  @Bean(name = "primaryDataSource", initMethod = "init", destroyMethod = "close")
  @ConfigurationProperties("spring.datasource.primary")
  public DruidDataSource dataSource() {
    DruidDataSource datasource = DruidDataSourceBuilder.create().build();
    datasource.setDefaultAutoCommit(false);
    return datasource;
  }

  @Primary
  @Bean(name = "dynamicDataSource")
  public DynamicDataSource dynamicDataSource(
      @Qualifier("primaryDataSource") DruidDataSource primaryDataSource) {
    logger.info("=====初始化动态数据源=====");

    DynamicDataSource dynamicDataSource = new DynamicDataSource();
    // 默认数据源
    dynamicDataSource.setDefaultTargetDataSource(primaryDataSource);

    DBTemplate   dbTemplate = this.buildDbTemplate(primaryDataSource, JdbcConnFactory.class);
    List<Tenant> tenants;
    try (Database database = dbTemplate.createDatabase()) {
      tenants = database.createOrm().find(Tenant.class, new Sql("SELECT * FROM tb_tenant"));
    }

    Map<Object, Object> customDataSources = new HashMap<>();

    if (DynamicDataSource.connectProperties == null)
      DynamicDataSource.connectProperties = primaryDataSource.getConnectProperties();

    for (Tenant tenant : tenants) {
      DruidDataSource ds = new DruidDataSource();
      ds.setConnectProperties(DynamicDataSource.connectProperties);
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
      @Qualifier("dynamicDataSource") DataSource dataSource) {

    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  public DBTemplate dbTemplate(@Qualifier("dynamicDataSource") DataSource dataSource) {
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
                    .setDbType(DbType.SQL_SERVER_2012)
                    .setBatchSize(200)
                    .setFetchSize(100)
                    .setDataCenterId(1L)
                    .setWorkerId(1L)
                    .setPageDataField("list")
                    .setPageCurrentField("pageNumber")
                    .setPageSizeField("pageSize")
                    .setPageTotalPageField("totalPage")
                    .setPageTotalRecordField("totalRow")
                    .setPageHasNextField("next")
                    .setShowSql(false)
                    .setShowResult(false))
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
