package work.myfavs.framework.orm.generator;

import java.sql.Connection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;

public class CodeGeneratorTest {

  String url      = "jdbc:mysql://127.0.0.1:3306/myfavs_test?useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
  String user     = "root";
  String password = "root";

  private Connection connection = null;

  @Before
  public void setUp()
      throws Exception {

  }

  @Test
  public void build() {

    GeneratorConfig config = new GeneratorConfig();
    config.setJdbcUrl(url);
    config.setJdbcUser(user);
    config.setJdbcPwd(password);
    config.setEntitiesPackage("work.myfavs.erp.domain.entity");
    config.setRepositoriesPackage("work.myfavs.erp.repository");

    config.getTypeMapper().put("varchar", new TypeDefinition("java.lang.String"));
    config.getTypeMapper().put("datetime", new TypeDefinition("java.util.Date"));
    config.getTypeMapper().put("decimal", new TypeDefinition("java.math.BigDecimal"));
    config.getTypeMapper().put("bigint", new TypeDefinition("java.lang.Long", "long"));
    config.getTypeMapper().put("int", new TypeDefinition("java.lang.Integer", "int"));
    config.getTypeMapper().put("bit", new TypeDefinition("java.lang.Boolean", "boolean"));

    CodeGenerator codeGenerator = new CodeGenerator(config);
    codeGenerator.build();
  }

  @After
  public void tearDown()
      throws Exception {

  }

}