package work.myfavs.framework.orm;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;
import work.myfavs.framework.orm.util.id.PKGenerator;
import work.myfavs.framework.orm.util.exception.DBException;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
    throw new DBException("The DataSource named %s not exists.", dsName);
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
    if (Objects.isNull(mapper) || mapper.map.isEmpty()) {
      PropertyHandlerFactory.registerDefault();
      return;
    }
    for (Entry<Class<?>, PropertyHandler> entry : mapper.map.entrySet()) {
      PropertyHandlerFactory.register(entry.getKey(), entry.getValue());
    }
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

      Constructor<? extends ConnFactory> constructor = cls.getDeclaredConstructor(DataSource.class);
      return constructor.newInstance(dataSource);
    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new DBException("Error create ConnFactory instance : %s", e.getMessage());
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

  /**
   * 创建 {@link Page} 对象
   *
   * @param <TView> 分页对象数据类型泛型
   * @return {@link Page} 对象
   */
  public <TView> Page<TView> createPage() {
    return new Page<>(this);
  }

  /**
   * 创建 {@link Page} 对象
   *
   * @param data         分页数据
   * @param currentPage  当前页码
   * @param pageSize     每页记录数
   * @param totalPages   总页数
   * @param totalRecords 总记录数
   * @param <TView>      分页对象数据类型泛型
   * @return {@link Page} 对象
   */
  public <TView> Page<TView> createPage(List<TView> data, long currentPage, long pageSize, long totalPages, long totalRecords) {
    Page<TView> page = createPage();
    page.setData(data);
    page.setCurrentPage(currentPage);
    page.setPageSize(pageSize);
    page.setTotalPages(totalPages);
    page.setTotalRecords(totalRecords);
    return page;
  }

  /**
   * 创建 {@link PageLite} 对象
   *
   * @param <TModel> 简单分页对象泛型
   * @return {@link PageLite} 对象
   */
  public <TModel> PageLite<TModel> createPageLite() {
    return new PageLite<>(this);
  }

  /**
   * 创建 {@link PageLite} 对象
   *
   * @param data        分页数据
   * @param currentPage 当前页码
   * @param pageSize    每页记录数
   * @param <TModel>    简单分页对象泛型
   * @return {@link PageLite} 对象
   */
  public <TModel> PageLite<TModel> createPageLite(
      List<TModel> data, long currentPage, long pageSize) {

    PageLite<TModel> instance = createPageLite();
    instance.setData(data);
    instance.setCurrentPage(currentPage);
    instance.setPageSize(pageSize);
    if (Objects.nonNull(data)) {
      instance.setHasNext(data.size() == pageSize);
    }
    return instance;
  }

  public static class Builder {

    private final String     dsName;
    private       DataSource dataSource;
    private       DBConfig   config;
    public        Mapper     mapper = new Mapper();

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

      if (Objects.isNull(this.config)) {
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
