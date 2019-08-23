package work.myfavs.framework.orm.generator;

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

    GeneratorConfig config = new GeneratorConfig();
    config.setDbType(DbType.MYSQL);
    config.setJdbcUrl(url);
    config.setJdbcUser(user);
    config.setJdbcPwd(password);
    config.setRootPath("D:");

    config.setGenEntities(true);
    config.setCoverEntitiesIfExists(true);
    config.setEntitiesPackage("work.myfavs.erp.domain.entity");

    config.setGenRepositories(true);
    config.setCoverRepositoriesIfExists(false);
    config.setRepositoriesPackage("work.myfavs.erp.repository");

    config.getTypeMapper().put("varchar", new TypeDefinition("java.lang.String"));
    config.getTypeMapper().put("datetime", new TypeDefinition("java.util.Date"));
    config.getTypeMapper().put("decimal", new TypeDefinition("java.math.BigDecimal", "BigDecimal.ZERO"));
    config.getTypeMapper().put("bigint", new TypeDefinition("java.lang.Long", "long", "0L"));
    config.getTypeMapper().put("int", new TypeDefinition("java.lang.Integer", "int", "0"));
    config.getTypeMapper().put("bit", new TypeDefinition("java.lang.Boolean", "boolean", "false"));

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