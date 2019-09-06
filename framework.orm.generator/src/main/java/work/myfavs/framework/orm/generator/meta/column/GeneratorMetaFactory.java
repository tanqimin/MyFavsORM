package work.myfavs.framework.orm.generator.meta.column;

import work.myfavs.framework.orm.generator.GeneratorConfig;
import work.myfavs.framework.orm.generator.meta.GeneratorMeta;
import work.myfavs.framework.orm.generator.meta.TableDefinition;
import work.myfavs.framework.orm.generator.util.GeneratorUtil;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

public abstract class GeneratorMetaFactory {

  private GeneratorMeta   generatorMeta   = null;
  private GeneratorConfig generatorConfig = null;

  protected GeneratorMetaFactory() {

  }

  public static GeneratorMetaFactory createInstance(GeneratorConfig generatorConfig) {

    GeneratorMetaFactory generatorMetaFactory = null;

    String dbType = generatorConfig.getDbType();
    if (DbType.MYSQL.equals(dbType)) {
      generatorMetaFactory = new MySQLGeneratorMetaFactory(generatorConfig);
    } else {
      throw new DBException(StringUtil.format("暂不支持 {} 数据库！", dbType));
    }
    generatorMetaFactory.generatorConfig = generatorConfig;
    return generatorMetaFactory;
  }

  public GeneratorMeta getGeneratorMeta() {

    if (this.generatorMeta == null) {
      this.generatorMeta = createGeneratorMeta();
      this.handlePrefix();
    }

    return this.generatorMeta;
  }

  private void handlePrefix() {

    String prefix = generatorConfig.getTablePrefix();
    if (prefix == null || prefix.length() == 0) {
      return;
    }
    for (TableDefinition tableDefinition : this.generatorMeta.getTableDefinitions()) {
      tableDefinition.setClassName(GeneratorUtil.toClass(tableDefinition.getTableName(), prefix));
    }
  }

  /**
   * 获取 GeneratorMeta
   *
   * @return GeneratorMeta
   */
  abstract protected GeneratorMeta createGeneratorMeta();

}
