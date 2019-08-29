package work.myfavs.framework.orm.generator;

import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.meta.DbType;

public class CodeGeneratorTest {

  CodeGenerator codeGenerator;


  @Before
  public void setUp()
      throws Exception {

    String url      = "jdbc:mysql://127.0.0.1:3306/myfavs_test?useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
    String user     = "root";
    String password = "root";

    GeneratorConfig             config     = new GeneratorConfig();
    Map<String, TypeDefinition> typeMapper = config.getTypeMapper();

    config.setDbType(DbType.MYSQL);                                       //数据库类型
    config.setJdbcUrl(url);                                               //数据库URL
    config.setJdbcUser(user);                                             //数据库用户
    config.setJdbcPwd(password);                                          //数据库密码
    config.setRootPath("D:");                                             //代码输出根目录

    config.setGenEntities(true);                                          //是否生成实体
    config.setCoverEntitiesIfExists(true);                                //实体存在时是否覆盖？
    config.setEntitiesPackage("work.myfavs.erp.domain.entity");           //实体Package名称

    config.setGenRepositories(true);                                      //是否生成Repository
    config.setCoverRepositoriesIfExists(false);                           //Repository存在时是否覆盖？
    config.setRepositoriesPackage("work.myfavs.erp.repository");          //Repository Package名称

    //注册生成器类型
    typeMapper.put("varchar", new TypeDefinition("java.lang.String"));
    typeMapper.put("datetime", new TypeDefinition("java.util.Date"));
    typeMapper.put("decimal", new TypeDefinition("java.math.BigDecimal", "BigDecimal.ZERO"));
    typeMapper.put("bigint", new TypeDefinition("java.lang.Long", "long", "0L"));
    typeMapper.put("int", new TypeDefinition("java.lang.Integer", "int", "0"));
    typeMapper.put("bit", new TypeDefinition("java.lang.Boolean", "boolean", "false"));

    codeGenerator = new CodeGenerator(config);
  }

  @Test
  public void genEntities() {

    codeGenerator.genEntities();
    codeGenerator.genRepositories();
  }

  @Test
  public void genRepositories() {

//    codeGenerator.genRepositories();
  }

  @After
  public void tearDown()
      throws Exception {

  }

}