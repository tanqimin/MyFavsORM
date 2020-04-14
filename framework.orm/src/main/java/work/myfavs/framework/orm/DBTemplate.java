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
  private DBConfig DBConfig = new DBConfig();
  //数据库连接工厂
  private Class<? extends ConnFactory> connectionFactory = null;
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
    this.DBConfig = builder.DBConfig;
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
  public DB open() {

    return this.open(null);
  }

  private DB open(Consumer<Connection> consumer) {

    DB db = new DB(this);
    Connection connection = db.open();
    if (consumer != null) {
      consumer.accept(connection);
    }
    return db;
  }

  @Override
  public void close() {

  }


  /**
   * 创建 Database 对象，并开启事务
   *
   * @return Database
   */
  public DB beginTransaction() {

    return this.beginTransaction(this.DBConfig.getDefaultIsolation());
  }

  /**
   * 创建 Database 对象，并开启事务
   *
   * @param transactionIsolation 事务隔离级别
   *
   * @return Database
   */
  public DB beginTransaction(int transactionIsolation) {

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
  public Class<? extends ConnFactory> getConnectionFactory() {

    return connectionFactory;
  }

  /**
   * 获取配置
   *
   * @return 配置
   */
  public DBConfig getDBConfig() {

    return DBConfig;
  }

  public static class Builder {

    private DataSource    dataSource;
    private DBConfig DBConfig;
    public  Mapper        mapper = new Mapper();

    private Class<? extends ConnFactory> connectionFactory = JdbcConnFactory.class;

    public Builder dataSource(DataSource dataSource) {

      this.dataSource = dataSource;
      return this;
    }

    public Builder config(Consumer<DBConfig> consumer) {

      DBConfig = new DBConfig();
      consumer.accept(DBConfig);
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

      if (this.dataSource == null) {
        throw new DBException("Please set a dataSource.");
      }

      if (this.DBConfig == null) {
        this.DBConfig = new DBConfig();
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
