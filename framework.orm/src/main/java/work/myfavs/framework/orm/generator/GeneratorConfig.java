package work.myfavs.framework.orm.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.Data;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;

@Data
public class GeneratorConfig {

  private Map<String, TypeDefinition> typeMapper = new HashMap<>();

  private String dbType;
  private String jdbcUrl;
  private String jdbcUser;
  private String jdbcPwd;

  private String rootPath = "";

  private boolean genEntities           = true;
  private boolean coverEntitiesIfExists = true;
  private String  entitiesPackage       = "";

  private boolean genRepositories           = false;
  private boolean coverRepositoriesIfExists = false;
  private String  repositoriesPackage       = "";

  public Set<String> getImportList() {

    Set<String> importsList = new TreeSet<>();
    for (TypeDefinition typeDefinition : typeMapper.values()) {
      importsList.add(typeDefinition.getName());
    }
    return importsList;
  }

}
