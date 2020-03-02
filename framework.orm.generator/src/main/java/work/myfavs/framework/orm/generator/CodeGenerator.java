package work.myfavs.framework.orm.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.CharsetUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import work.myfavs.framework.orm.generator.meta.GeneratorMeta;
import work.myfavs.framework.orm.generator.meta.TableDefinition;
import work.myfavs.framework.orm.generator.meta.column.ColumnDefinition;
import work.myfavs.framework.orm.generator.meta.column.GeneratorMetaFactory;
import work.myfavs.framework.orm.generator.util.GeneratorUtil;
import work.myfavs.framework.orm.generator.util.PathUtil;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

/**
 * 代码生成器
 */
public class CodeGenerator {

  //代码生成器配置
  private GeneratorConfig   generatorConfig;
  //代码生成器模板
  private GeneratorTemplate generatorTemplate;
  //生成器元数据
  private GeneratorMeta     generatorMeta;

  public GeneratorConfig getGeneratorConfig() {

    return generatorConfig;
  }

  public void setGeneratorConfig(GeneratorConfig generatorConfig) {

    this.generatorConfig = generatorConfig;
  }

  public GeneratorTemplate getGeneratorTemplate() {

    return generatorTemplate;
  }

  public void setGeneratorTemplate(GeneratorTemplate generatorTemplate) {

    this.generatorTemplate = generatorTemplate;
  }

  public GeneratorMeta getGeneratorMeta() {

    return generatorMeta;
  }

  public void setGeneratorMeta(GeneratorMeta generatorMeta) {

    this.generatorMeta = generatorMeta;
  }

  public CodeGenerator(GeneratorConfig generatorConfig) {

    this.generatorConfig = generatorConfig;
    this.generatorTemplate = new GeneratorTemplate();
    this.generatorMeta = GeneratorMetaFactory.createInstance(generatorConfig)
                                             .getGeneratorMeta();
  }

  /**
   * 生成实体类
   */
  public void genEntities() {

    if (generatorConfig.isGenEntity()) {
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
  private void outputEntity(String tableName,
                            List<ColumnDefinition> columns) {

    String entitiesPackage = generatorConfig.getEntityPackage();            //实体Package
    String prefix          = generatorConfig.getTablePrefix();                     //忽略数据表前缀

    String         className      = GeneratorUtil.toClass(tableName, prefix);        //实体类名称
    GenerationType generationType = generatorConfig.getGenerationType();
    boolean        coverIfExist   = generatorConfig.isCoverEntityIfExists();

    Map<String, Object> params = new HashMap<>();                             //模板参数

    params.put("package", entitiesPackage);
    params.put("table", tableName);
    params.put("class", className);
    params.put("columns", columns);
    params.put("generationType", generationType.getName());
    params.put("imports", generatorConfig.getImportList());                   //Import类列表

    String render = generatorTemplate.render("/entity.txt", params);

    outputFile(getFilePath(entitiesPackage, className), render, coverIfExist);
  }

  /**
   * 创建代码文件路径
   *
   * @param packageName 代码所在的Package
   * @param fileName    文件名
   *
   * @return 代码文件路径
   */
  private String getFilePath(String packageName,
                             String fileName) {

    StringBuilder res      = new StringBuilder();
    String        rootPath = generatorConfig.getTemplateDir();

    if (rootPath != null && rootPath.length() > 0) {
      res.append(rootPath);
      if (!rootPath.endsWith("/")) {
        res.append("/");
      }
    }
    return res.append("src/main/java/")
              .append(PathUtil.toPath(packageName))
              .append("/")
              .append(fileName)
              .append(".java")
              .toString();
  }

  /**
   * 输出文件
   *
   * @param filePath 文件路径
   * @param context  文件内容
   */
  private void outputFile(String filePath,
                          String context,
                          boolean isCover) {

    if (FileUtil.exist(filePath) && !isCover) {
      return;
    }
    FileWriter fileWriter = new FileWriter(filePath, CharsetUtil.UTF_8);
    fileWriter.write(context);
  }

  /**
   * 生成Repository类
   */
  public void genRepositories() {

    if (!generatorConfig.isGenRepository()) {
      return;
    }
    String  repositoriesPackage = this.generatorConfig.getRepositoryPackage();
    String  entitiesPackage     = this.generatorConfig.getEntityPackage();
    boolean coverIfExist        = this.generatorConfig.isCoverRepositoryIfExists();

    String              queryPackage      = repositoriesPackage.concat(".query");
    String              repositoryPackage = repositoriesPackage.concat(".repo");
    Map<String, Object> params            = new HashMap<>();
    params.put("entitiesPackage", entitiesPackage);
    params.put("repositoriesPackage", repositoriesPackage);
    params.put("queryPackage", queryPackage);
    params.put("repositoryPackage", repositoryPackage);

    String render = generatorTemplate.render("/baseQuery.txt", params);
    outputFile(getFilePath(repositoriesPackage, "BaseQuery"), render, coverIfExist);

    render = generatorTemplate.render("/baseRepository.txt", params);
    outputFile(getFilePath(repositoriesPackage, "BaseRepository"), render, coverIfExist);

    for (TableDefinition tableDefinition : generatorMeta.getTableDefinitions()) {
      final String entity = tableDefinition.getClassName();
      params.put("class", entity);
      params.put("columns", tableDefinition.getColumns());

      render = generatorTemplate.render("/query.txt", params);
      outputFile(getFilePath(queryPackage, entity + "Query"), render, coverIfExist);

      render = generatorTemplate.render("/repository.txt", params);
      outputFile(getFilePath(repositoryPackage, entity + "Repository"), render, coverIfExist);
    }

  }

}
