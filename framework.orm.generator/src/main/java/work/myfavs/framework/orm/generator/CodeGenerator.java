package work.myfavs.framework.orm.generator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import work.myfavs.framework.orm.generator.meta.GeneratorMeta;
import work.myfavs.framework.orm.generator.meta.TableDefinition;
import work.myfavs.framework.orm.generator.meta.column.ColumnDefinition;
import work.myfavs.framework.orm.generator.meta.column.GeneratorMetaFactory;
import work.myfavs.framework.orm.generator.util.FileUtil;
import work.myfavs.framework.orm.generator.util.GeneratorUtil;
import work.myfavs.framework.orm.generator.util.PathUtil;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 代码生成器
 */
@Data
public class CodeGenerator {

  //代码生成器配置
  private GeneratorConfig   generatorConfig;
  //代码生成器模板
  private GeneratorTemplate generatorTemplate;
  //生成器元数据
  private GeneratorMeta     generatorMeta;

  public CodeGenerator(GeneratorConfig generatorConfig) {

    this.generatorConfig = generatorConfig;
    this.generatorTemplate = new GeneratorTemplate();
    this.generatorMeta = GeneratorMetaFactory.createInstance(generatorConfig).getGeneratorMeta();
  }

  /**
   * 生成实体类
   */
  public void genEntities() {

    if (generatorConfig.isGenEntities()) {
      for (TableDefinition tableDefinition : generatorMeta.getTableDefinitions()) {
        outputEntity(tableDefinition.getTableName(), tableDefinition.getColumns());
      }
    }

  }

  /**
   * 输出实体类
   *
   * @param tableName 数据表名称
   * @param columns   数据列集合
   */
  private void outputEntity(@NonNull String tableName, @NonNull List<ColumnDefinition> columns) {

    String         entitiesPackage = generatorConfig.getEntitiesPackage();            //实体Package
    String         prefix          = generatorConfig.getPrefix();                     //忽略数据表前缀
    String         className       = GeneratorUtil.toClass(tableName, prefix);        //实体类名称
    GenerationType generationType  = generatorConfig.getGenerationType();

    Map<String, Object> params = new HashMap<>();                             //模板参数

    params.put("package", entitiesPackage);
    params.put("table", tableName);
    params.put("class", className);
    params.put("columns", columns);
    params.put("generationType", generationType.getName());
    params.put("imports", generatorConfig.getImportList());                   //Import类列表

    String render = generatorTemplate.render("/entities.txt", params);

    outputFile(getFilePath(entitiesPackage, className), render);
  }

  /**
   * 创建代码文件路径
   *
   * @param packageName 代码所在的Package
   * @param fileName    文件名
   *
   * @return 代码文件路径
   */
  private String getFilePath(String packageName, String fileName) {

    StringBuilder res      = new StringBuilder();
    String        rootPath = generatorConfig.getRootPath();
    if (rootPath != null && rootPath.length() > 0) {
      res.append(rootPath);
      if (!rootPath.endsWith("/")) {
        res.append("/");
      }
    }

    return res.append("src/main/java/").append(PathUtil.toPath(packageName)).append("/").append(fileName).append(".java").toString();
  }

  /**
   * 输出文件
   *
   * @param filePath 文件路径
   * @param context  文件内容
   */
  private void outputFile(String filePath, String context) {

    try {
      int oper = generatorConfig.isCoverEntitiesIfExists()
          ? FileUtil.OVERWRITE
          : FileUtil.IGNORE;
      FileUtil.TextToFile(filePath, context, oper);
    } catch (IOException e) {
      throw new DBException(e);
    }

  }

  /**
   * 生成Repository类
   */
  public void genRepositories() {

    if (!generatorConfig.isGenRepositories()) {
      return;
    }
    String repositoriesPackage = this.generatorConfig.getRepositoriesPackage();
    String entitiesPackage     = generatorConfig.getEntitiesPackage();

    String              queryPackage      = repositoriesPackage.concat(".query");
    String              repositoryPackage = repositoriesPackage.concat(".repo");
    Map<String, Object> params            = new HashMap<>();
    params.put("repositoriesPackage", repositoriesPackage);
    params.put("queryPackage", queryPackage);
    params.put("repositoryPackage", repositoryPackage);

    String render = generatorTemplate.render("/baseQuery.txt", params);
    outputFile(getFilePath(repositoriesPackage, "BaseQuery"), render);

    render = generatorTemplate.render("/baseRepository.txt", params);
    outputFile(getFilePath(repositoriesPackage, "BaseRepository"), render);

    for (String entity : generatorMeta.getEntities()) {

      params.put("entitiesPackage", entitiesPackage);
      params.put("class", entity);

      render = generatorTemplate.render("/query.txt", params);
      outputFile(getFilePath(queryPackage, entity + "Query"), render);

      render = generatorTemplate.render("/repository.txt", params);
      outputFile(getFilePath(repositoryPackage, entity + "Repository"), render);
    }
  }

}
