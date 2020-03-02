package work.myfavs.framework.orm.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

public class GeneratorConfig {

  private Map<String, TypeDefinition> typeMapper = new HashMap<>();

  private String dbType;
  private String jdbcUrl;
  private String jdbcUser;
  private String jdbcPwd;

  private String templateDir = "";                                                      //模板路径
  private String tablePrefix = "";                                                      //忽略的表前缀

  private boolean genEntity           = true;
  private boolean coverEntityIfExists = true;
  private String  entityPackage       = "";

  private boolean        genRepository           = false;
  private boolean        coverRepositoryIfExists = false;
  private String         repositoryPackage       = "";
  private GenerationType generationType          = GenerationType.SNOW_FLAKE;

  public Map<String, TypeDefinition> getTypeMapper() {

    return typeMapper;
  }

  public void setTypeMapper(Map<String, TypeDefinition> typeMapper) {

    this.typeMapper = typeMapper;
  }

  public String getDbType() {

    return dbType;
  }

  public void setDbType(String dbType) {

    this.dbType = dbType;
  }

  public String getJdbcUrl() {

    return jdbcUrl;
  }

  public void setJdbcUrl(String jdbcUrl) {

    this.jdbcUrl = jdbcUrl;
  }

  public String getJdbcUser() {

    return jdbcUser;
  }

  public void setJdbcUser(String jdbcUser) {

    this.jdbcUser = jdbcUser;
  }

  public String getJdbcPwd() {

    return jdbcPwd;
  }

  public void setJdbcPwd(String jdbcPwd) {

    this.jdbcPwd = jdbcPwd;
  }

  public String getTemplateDir() {

    return templateDir;
  }

  public void setTemplateDir(String templateDir) {

    this.templateDir = templateDir;
  }

  public String getTablePrefix() {

    return tablePrefix;
  }

  public void setTablePrefix(String tablePrefix) {

    this.tablePrefix = tablePrefix;
  }

  public boolean isGenEntity() {

    return genEntity;
  }

  public void setGenEntity(boolean genEntity) {

    this.genEntity = genEntity;
  }

  public boolean isCoverEntityIfExists() {

    return coverEntityIfExists;
  }

  public void setCoverEntityIfExists(boolean coverEntityIfExists) {

    this.coverEntityIfExists = coverEntityIfExists;
  }

  public String getEntityPackage() {

    return entityPackage;
  }

  public void setEntityPackage(String entityPackage) {

    this.entityPackage = entityPackage;
  }

  public boolean isGenRepository() {

    return genRepository;
  }

  public void setGenRepository(boolean genRepository) {

    this.genRepository = genRepository;
  }

  public boolean isCoverRepositoryIfExists() {

    return coverRepositoryIfExists;
  }

  public void setCoverRepositoryIfExists(boolean coverRepositoryIfExists) {

    this.coverRepositoryIfExists = coverRepositoryIfExists;
  }

  public String getRepositoryPackage() {

    return repositoryPackage;
  }

  public void setRepositoryPackage(String repositoryPackage) {

    this.repositoryPackage = repositoryPackage;
  }

  public GenerationType getGenerationType() {

    return generationType;
  }

  public void setGenerationType(GenerationType generationType) {

    this.generationType = generationType;
  }

  public Set<String> getImportList() {

    Set<String> importsList = new TreeSet<>();
    for (TypeDefinition typeDefinition : typeMapper.values()) {
      importsList.add(typeDefinition.getName());
    }
    return importsList;
  }

}
