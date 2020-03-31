package work.myfavs.framework.orm;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import javax.sql.DataSource;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.exception.DBException;


public class DBTemplate
    implements Cloneable, AutoCloseable {

  //region Attributes
  //数据源
  private DataSource                         dataSource;
  //数据库配置
  private Configuration                      configuration     = new Configuration();
  //数据库连接工厂
  private Class<? extends ConnectionFactory> connectionFactory = null;
  //类型解析器
  private Mapper                             mapper;
  //endregion

  //region Constructor

  /**
   * 构造方法
   *
   * @param builder Builder
   */
  private DBTemplate(Builder builder) {

    this.dataSource = builder.dataSource;
    this.configuration = builder.configuration;
    this.connectionFactory = builder.connectionFactory;
    this.mapper = builder.mapper;

    //注册 PropertyHandler
    if (mapper == null || mapper.map.isEmpty()) {
      PropertyHandlerFactory.registerDefault();
    } else {
      for (Entry<Class<?>, PropertyHandler> entry : mapper.map.entrySet()) {
        PropertyHandlerFactory.register(entry.getKey(), entry.getValue());
      }
    }
  }
  //endregion


  /**
   * 创建 Database 对象
   *
   * @return Database
   */
  public Database open() {

    return this.open(null);
  }

  private Database open(Consumer<Connection> consumer) {

    Database   database   = new Database(this);
    Connection connection = database.open();
    if (consumer != null) {
      consumer.accept(connection);
    }
    return database;
  }

  @Override
  public void close() {

  }


  /**
   * 创建 Database 对象，并开启事务
   *
   * @return Database
   */
  public Database beginTransaction() {

    return this.beginTransaction(this.configuration.getDefaultIsolation());
  }

  /**
   * 创建 Database 对象，并开启事务
   *
   * @param transactionIsolation 事务隔离级别
   *
   * @return Database
   */
  public Database beginTransaction(int transactionIsolation) {

    return this.open(connection -> {
      try {
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(transactionIsolation);
      } catch (SQLException e) {
        throw new DBException(e, "Could not start the transaction, error message: ");
      }
    });
  }

  private void setTransactionIsolation(Connection conn,
                                       int isolation) {

    try {
      conn.setTransactionIsolation(isolation);
    } catch (SQLException e) {
      throw new DBException(e, "Could not set the transaction isolation, error message: ");
    }
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
  public Class<? extends ConnectionFactory> getConnectionFactory() {

    return connectionFactory;
  }

  /**
   * 获取配置
   *
   * @return 配置
   */
  public Configuration getConfiguration() {

    return configuration;
  }

  public static class Builder {

    private DataSource    dataSource;
    private Configuration configuration;
    public  Mapper        mapper = new Mapper();

    private Class<? extends ConnectionFactory> connectionFactory = JdbcConnectionFactory.class;

    public Builder dataSource(DataSource dataSource) {

      this.dataSource = dataSource;
      return this;
    }

    public Builder config(Consumer<Configuration> consumer) {

      configuration = new Configuration();
      consumer.accept(configuration);
      return this;
    }

    public Builder connectionFactory(Class<? extends ConnectionFactory> connectionFactory) {

      this.connectionFactory = connectionFactory;
      return this;
    }

    public Builder mapping(Consumer<Mapper> consumer) {

      consumer.accept(mapper);
      return this;
    }

    public DBTemplate build() {

      if (this.dataSource == null) {
        throw new DBException("Please set a dataSource.");
      }

      if (this.configuration == null) {
        this.configuration = new Configuration();
      }
      return new DBTemplate(this);
    }

  }

  public static class Mapper {

    private Map<Class<?>, PropertyHandler> map;

    private Mapper() {

      map = new HashMap<>();
    }

    public Mapper register(Class<?> clazz,
                           PropertyHandler propertyHandler) {

      map.put(clazz, propertyHandler);
      return this;
    }

  }

}
