package work.myfavs.framework.sb2.demo.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import work.myfavs.framework.orm.*;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.meta.handler.impls.*;
import work.myfavs.framework.orm.util.lang.NVarchar;
import work.myfavs.framework.sb2.demo.common.datasource.DynamicDataSource;
import work.myfavs.framework.sb2.demo.tenant.Tenant;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多租户数据源配置，初始化主数据源并加载各租户的动态数据源.
 */
@Configuration
public class DataSourceConfig {
  private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

  /**
   * 创建主数据源.
   *
   * @return 主数据源实例
   */
  @Bean(name = "primaryDataSource", initMethod = "init", destroyMethod = "close")
  @ConfigurationProperties("spring.datasource.primary")
  public DruidDataSource dataSource() {
    DruidDataSource datasource = DruidDataSourceBuilder.create().build();
    datasource.setDefaultAutoCommit(false);
    return datasource;
  }

  /**
   * 创建动态数据源，从主数据库加载租户列表并初始化各租户的数据源.
   *
   * @param primaryDataSource 主数据源
   * @return 动态数据源实例
   */
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

    if (null == DynamicDataSource.getConnectProperties())
      DynamicDataSource.setConnectProperties(primaryDataSource.getConnectProperties());

    for (Tenant tenant : tenants) {
      DruidDataSource ds = new DruidDataSource();
      ds.setConnectProperties(DynamicDataSource.getConnectProperties());
      ds.setDriverClassName(tenant.getJdbcClass());
      ds.setUrl(tenant.getJdbcUrl());
      ds.setUsername(tenant.getJdbcUser());
      ds.setPassword(tenant.getJdbcPassword());
      customDataSources.put(tenant.getTenant(), ds);
      logger.info("已加载租户库数据源:{}", tenant.getTenant());
    }

    dynamicDataSource.setTargetDataSources(customDataSources);
    return dynamicDataSource;
  }

  /**
   * 创建数据源事务管理器，使用动态数据源.
   *
   * @param dataSource 动态数据源
   * @return 数据源事务管理器
   */
  @Bean
  @Primary
  public DataSourceTransactionManager dataSourceTransactionManager(
      @Qualifier("dynamicDataSource") DataSource dataSource) {

    return new DataSourceTransactionManager(dataSource);
  }

  /**
   * 创建 DBTemplate 实例.
   *
   * @param dataSource 数据源
   * @return DBTemplate 实例
   */
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
                    .register(NVarchar.class, new NVarcharPropertyHandler())
                    .register(BigDecimal.class, new BigDecimalPropertyHandler())
                    .register(Long.class, new LongPropertyHandler())
                    .register(long.class, new LongPropertyHandler(true))
                    .register(Boolean.class, new BooleanPropertyHandler())
                    .register(boolean.class, new BooleanPropertyHandler(true))
                    .register(int.class, new IntegerPropertyHandler(true))
                    .register(Date.class, new DatePropertyHandler()))
        .build();
  }
}
