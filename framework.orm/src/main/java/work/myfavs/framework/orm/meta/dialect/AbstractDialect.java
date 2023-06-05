package work.myfavs.framework.orm.meta.dialect;

public abstract class AbstractDialect {
  /**
   * 子类需实现获取数据库类型的方法
   *
   * @return 数据库类型，参照 work.myfavs.framework.orm.meta.DbType 定义
   */
  public abstract String dbType();
}
