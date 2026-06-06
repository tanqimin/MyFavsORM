package work.myfavs.framework.orm;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.id.PKGenerator;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 数据库配置
 *
 * @author tanqimin
 */
@SuppressWarnings("rawtypes")
public class DBTemplate {
  private static final Map<String/* dsName */, DBTemplate> POOL = new ConcurrentHashMap<>();

  public static DBTemplate get(String dsName) {
    if (POOL.containsKey(dsName)) {
      return POOL.get(dsName);
    }
    throw new DBException("数据源 %s 不存在.", dsName);
  }

  public static DBTemplate add(String dsName, DBTemplate dbTemplate) {
    POOL.put(dsName, dbTemplate);
    return dbTemplate;
  }

  // region Attributes
  /**
   * 数据源名称
   */
  private final String      dsName;
  /**
   * 数据源
   */
  private final DataSource  dataSource;
  /**
   * 数据库配置
   */
  private final DBConfig    dbConfig;
  /**
   * 数据库连接工厂
   */
  private final ConnFactory connectionFactory;
  /**
   * 主键生成器
   */
  private final PKGenerator pkGenerator;
  // endregion

  // region Constructor

  /**
   * 构造方法
   *
   * @param builder Builder
   */
  private DBTemplate(Builder builder) {
    this.dsName = builder.dsName;
    this.dataSource = builder.dataSource;
    this.dbConfig = builder.config;
    this.connectionFactory = createConnFactory(builder.connectionFactory, builder.dataSource);
    this.pkGenerator =
        new PKGenerator(this.dbConfig.getWorkerId(), this.dbConfig.getDataCenterId());
    // 注册 PropertyHandler
    registerMapper(builder.mapper);
  }

  /**
   * 注册 PropertyHandler
   *
   * @param mapper Mapper
   */
  private void registerMapper(Mapper mapper) {
    if (mapper.map.isEmpty()) {
      PropertyHandlerFactory.registerDefault();
      return;
    }
    mapper.map.forEach(PropertyHandlerFactory::register);
  }
  // endregion

  /**
   * 获取数据源名称
   *
   * @return 数据源名称
   */
  public String getDsName() {
    return dsName;
  }

  /**
   * 获取数据源
   *
   * @return 数据源
   */
  public DataSource getDataSource() {

    return dataSource;
  }

  /**
   * 获取连接工厂类
   *
   * @return 连接工厂类
   */
  public ConnFactory getConnectionFactory() {

    return connectionFactory;
  }

  /**
   * 获取配置
   *
   * @return 配置
   */
  public DBConfig getDbConfig() {

    return dbConfig;
  }

  public PKGenerator getPkGenerator() {
    return pkGenerator;
  }

  /**
   * 获取数据库连接工厂
   *
   * @param cls        数据库连接工厂类型
   * @param dataSource 数据源
   * @return 数据库连接工厂
   */
  private ConnFactory createConnFactory(Class<? extends ConnFactory> cls, DataSource dataSource) {
    try {
      //使用cls反射创建 ConnFactory 的实例
      return cls.getDeclaredConstructor(DataSource.class).newInstance(dataSource);
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new DBException("创建 ConnFactory 实例时发生异常: %s", e.getMessage());
    }
  }

  /**
   * 创建 {@link Database} 对象
   *
   * @return {@link Database}
   */
  public Database createDatabase() {
    return new Database(this);
  }

  public static class Builder {

    private final String     dsName;
    private       DataSource dataSource;
    private       DBConfig   config;
    public final  Mapper     mapper = new Mapper();

    public Builder() {
      this(DBConfig.DEFAULT_DATASOURCE_NAME);
    }

    public Builder(String dsName) {
      this.dsName = dsName;
    }

    private Class<? extends ConnFactory> connectionFactory = JdbcConnFactory.class;

    public Builder dataSource(DataSource dataSource) {

      this.dataSource = dataSource;
      return this;
    }

    public Builder config(Consumer<DBConfig> consumer) {

      config = new DBConfig();
      consumer.accept(config);
      return this;
    }

    public Builder connectionFactory(Class<? extends ConnFactory> connectionFactory) {

      this.connectionFactory = connectionFactory;
      return this;
    }

    public Builder mapping(Consumer<Mapper> consumer) {

      consumer.accept(mapper);
      return this;
    }

    public DBTemplate build() {

      Objects.requireNonNull(this.dataSource, "DataSource is required.");

      if (null == this.config) {
        this.config = new DBConfig();
      }

      return DBTemplate.add(dsName, new DBTemplate(this));
    }
  }

  public static class Mapper {

    private final Map<Class<?>, PropertyHandler> map;

    private Mapper() {

      map = new HashMap<>();
    }

    public Mapper register(Class<?> clazz, PropertyHandler propertyHandler) {

      map.put(clazz, propertyHandler);
      return this;
    }
  }
}
