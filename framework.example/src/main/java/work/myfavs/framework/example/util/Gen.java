package work.myfavs.framework.example.util;

import java.util.Map;
import work.myfavs.framework.orm.generator.CodeGenerator;
import work.myfavs.framework.orm.generator.GeneratorConfig;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.meta.DbType;

public class Gen {

  public static void main(String[] args) {

    String url      = "jdbc:mysql://127.0.0.1:3306/myfavs_test?useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
    String user     = "root";
    String password = "root";

    GeneratorConfig             config     = new GeneratorConfig();
    Map<String, TypeDefinition> typeMapper = config.getTypeMapper();

    config.setDbType(DbType.MYSQL);                                       //数据库类型
    config.setJdbcUrl(url);                                               //数据库URL
    config.setJdbcUser(user);                                             //数据库用户
    config.setJdbcPwd(password);                                          //数据库密码
    config.setRootPath("D:/project/github/tanqimin@gmail.com/myfavs.framework/framework.example");                                             //代码输出根目录

    config.setPrefix("tb_");

    config.setGenEntities(true);                                          //是否生成实体
    config.setCoverEntitiesIfExists(false);                                //实体存在时是否覆盖？
    config.setEntitiesPackage("work.myfavs.framework.example.domain.entity");           //实体Package名称

    config.setGenRepositories(true);                                      //是否生成Repository
    config.setCoverRepositoriesIfExists(true);                           //Repository存在时是否覆盖？
    config.setRepositoriesPackage("work.myfavs.framework.example.repository");          //Repository Package名称

    //注册生成器类型
    typeMapper.put("varchar", new TypeDefinition("java.lang.String"));
    typeMapper.put("datetime", new TypeDefinition("java.util.Date"));
    typeMapper.put("decimal", new TypeDefinition("java.math.BigDecimal", "BigDecimal.ZERO"));
    typeMapper.put("bigint", new TypeDefinition("java.lang.Long", "long", "0L"));
    typeMapper.put("int", new TypeDefinition("java.lang.Integer", "int", "0"));
    typeMapper.put("bit", new TypeDefinition("java.lang.Boolean", "boolean", "false"));

    CodeGenerator codeGenerator = new CodeGenerator(config);
    codeGenerator.genEntities();
    codeGenerator.genRepositories();
  }

}
