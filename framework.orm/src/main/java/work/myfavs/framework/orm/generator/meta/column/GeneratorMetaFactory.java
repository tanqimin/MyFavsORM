package work.myfavs.framework.orm.generator.meta.column;

import work.myfavs.framework.orm.generator.GeneratorConfig;
import work.myfavs.framework.orm.generator.meta.GeneratorMeta;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

public abstract class GeneratorMetaFactory {

  private GeneratorMeta generatorMeta = null;

  protected GeneratorMetaFactory() {

  }

  public static GeneratorMetaFactory createInstance(GeneratorConfig generatorConfig) {

    String dbType = generatorConfig.getDbType();
    switch (dbType) {
      case DbType.MYSQL:
        return new MySQLGeneratorMetaFactory(generatorConfig);
      default:
        throw new DBException(StringUtil.format("暂不支持 {} 数据库！", dbType));
    }
  }

  public GeneratorMeta getGeneratorMeta() {

    if (this.generatorMeta == null) {
      this.generatorMeta = createGeneratorMeta();
    }
    return this.generatorMeta;
  }

  /**
   * 获取 GeneratorMeta
   *
   * @return GeneratorMeta
   */
  abstract protected GeneratorMeta createGeneratorMeta();

}
