package work.myfavs.framework.orm.generator.meta.column;

import work.myfavs.framework.orm.generator.GeneratorConfig;
import work.myfavs.framework.orm.generator.meta.GeneratorMeta;
import work.myfavs.framework.orm.generator.meta.TableDefinition;
import work.myfavs.framework.orm.generator.util.GeneratorUtil;
import work.myfavs.framework.orm.meta.DbType;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

public abstract class GeneratorMetaFactory {

  private GeneratorMeta   generatorMeta   = null;
  private GeneratorConfig generatorConfig = null;

  protected GeneratorMetaFactory() {}

  public static GeneratorMetaFactory createInstance(GeneratorConfig generatorConfig) {

    GeneratorMetaFactory generatorMetaFactory;

    String dbType = generatorConfig.getDbType();
    if (DbType.MYSQL.equals(dbType)) {
      generatorMetaFactory = new MySQLGeneratorMetaFactory(generatorConfig);
    } else {
      throw new DBException("不支持的数据库类型: %s. ", dbType);
    }
    generatorMetaFactory.generatorConfig = generatorConfig;
    return generatorMetaFactory;
  }

  public GeneratorMeta getGeneratorMeta() {

    if (null == this.generatorMeta) {
      this.generatorMeta = createGeneratorMeta();
      this.handlePrefix();
    }

    return this.generatorMeta;
  }

  private void handlePrefix() {

    String prefix = generatorConfig.getTablePrefix();
    if (StringUtil.isEmpty(prefix)) {
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
  protected abstract GeneratorMeta createGeneratorMeta();
}
