package work.myfavs.framework.orm.generator;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import lombok.Data;
import work.myfavs.framework.orm.generator.meta.SchemaColumn;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.generator.meta.sql.MetaSql;
import work.myfavs.framework.orm.generator.util.FileUtil;
import work.myfavs.framework.orm.generator.util.PathUtil;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.ResultSetUtil;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

@Data
public class CodeGenerator {

  private Map<String, List<SchemaColumn>> columnsMap;
  private Set<String>                     entities = new TreeSet<>();
  private GeneratorConfig                 generatorConfig;
  private GeneratorTemplate               generatorTemplate;

  public CodeGenerator(GeneratorConfig generatorConfig) {

    this.generatorConfig = generatorConfig;
    this.columnsMap = createColumnMap();
    this.generatorTemplate = new GeneratorTemplate();
  }

  public void genEntities() {

    String entitiesPackage = generatorConfig.getEntitiesPackage();

    for (Entry<String, List<SchemaColumn>> entry : columnsMap.entrySet()) {
      List<SchemaColumn> columns = entry.getValue();
      if (columns == null || columns.isEmpty()) {
        continue;
      }
      SchemaColumn column = columns.get(0);

      Map<String, Object> params = new HashMap<>();

      params.put("package", entitiesPackage);
      params.put("table", column.getTable());
      params.put("class", column.getClassName());
      params.put("columns", columns);
      params.put("imports", generatorConfig.getImportList());

      String render   = generatorTemplate.render("/entities.txt", params);
      String filePath = getFilePath(entitiesPackage, column.getClassName());

      output(filePath, render);
    }
  }

  private String getFilePath(String packageName,
                             String fileName) {

    return generatorConfig.getRootPath()
                          .concat("/src/main/java/")
                          .concat(PathUtil.toPath(packageName))
                          .concat("/")
                          .concat(fileName)
                          .concat(".java");
  }

  private void output(String filePath,
                      String context) {

    try {
      int oper = generatorConfig.isCoverEntitiesIfExists()
          ? FileUtil.OVERWRITE
          : FileUtil.IGNORE;
      FileUtil.TextToFile(filePath, context, oper);
    } catch (IOException e) {
      throw new DBException(e);
    }

  }

  public void genRepositories() {

    String repositoriesPackage = this.generatorConfig.getRepositoriesPackage();
    String entitiesPackage     = generatorConfig.getEntitiesPackage();

    String              queryPackage      = repositoriesPackage.concat(".query");
    String              repositoryPackage = repositoriesPackage.concat(".repo");
    Map<String, Object> params            = new HashMap<>();
    params.put("repositoriesPackage", repositoriesPackage);
    params.put("queryPackage", queryPackage);
    params.put("repositoryPackage", repositoryPackage);

    String render = generatorTemplate.render("/baseQuery.txt", params);
    output(getFilePath(repositoriesPackage, "BaseQuery"), render);

    render = generatorTemplate.render("/baseRepository.txt", params);
    output(getFilePath(repositoriesPackage, "BaseRepository"), render);

    for (String entity : entities) {

      params.put("entitiesPackage", entitiesPackage);
      params.put("class", entity);

      render = generatorTemplate.render("/query.txt", params);
      output(getFilePath(queryPackage, entity + "Query"), render);

      render = generatorTemplate.render("/repository.txt", params);
      output(getFilePath(repositoryPackage, entity + "Repository"), render);
    }
  }

  private Map<String, List<SchemaColumn>> createColumnMap() {

    final String jdbcUrl  = generatorConfig.getJdbcUrl();
    final String jdbcUser = generatorConfig.getJdbcUser();
    final String jdbcPwd  = generatorConfig.getJdbcPwd();

    Map<String, List<SchemaColumn>> res    = new HashMap<>();
    Connection                      conn   = null;
    PreparedStatement               ps     = null;
    ResultSet                       rs     = null;
    SchemaColumn                    column = null;
    try {
      conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPwd);
      Sql sql = MetaSql.get(conn);
      ps = DBUtil.getPs(conn, sql.getSql()
                                 .toString(), sql.getParams());
      rs = ps.executeQuery();

      while (rs.next()) {
        String table = ResultSetUtil.getString(rs, "table");
        column = convert(rs);

        if (res.containsKey(table)) {
          res.get(table)
             .add(column);
        } else {
          List<SchemaColumn> columns = new ArrayList<>();
          columns.add(column);
          res.put(table, columns);
        }

        this.entities.add(column.getClassName());
      }

      return res;
    } catch (SQLException e) {
      throw new DBException(e);
    } finally {
      DBUtil.close(conn, ps, rs);
    }
  }

  private SchemaColumn convert(ResultSet rs)
      throws SQLException {

    String  table    = ResultSetUtil.getString(rs, "table");
    String  column   = ResultSetUtil.getString(rs, "column");
    Boolean pk       = ResultSetUtil.getBoolean(rs, "pk");
    Boolean nullable = ResultSetUtil.getBoolean(rs, "nullable");
    Integer idx      = ResultSetUtil.getInt(rs, "idx");
    String  dataType = ResultSetUtil.getString(rs, "type");
    String  comment  = ResultSetUtil.getString(rs, "comment");

    SchemaColumn schemaColumn = new SchemaColumn();

    schemaColumn.setTable(table);
    schemaColumn.setColumn(column);
    schemaColumn.setPrimaryKey(pk);
    schemaColumn.setNullable(nullable);
    schemaColumn.setIndex(idx);
    schemaColumn.setDataType(dataType);
    schemaColumn.setTypeDefinition(createTypeDefinition(dataType, comment));
    schemaColumn.setComment(comment);
    return schemaColumn;
  }

  private TypeDefinition createTypeDefinition(String dataType,
                                              String comment) {

    if (comment != null) {
      if (comment.contains("#")) {
        String         className      = comment.split("#")[1];
        TypeDefinition typeDefinition = new TypeDefinition(className);
        generatorConfig.getTypeMapper()
                       .put(className, typeDefinition);
        return typeDefinition;
      }
    }

    for (Entry<String, TypeDefinition> entry : generatorConfig.getTypeMapper()
                                                              .entrySet()) {
      if (StringUtil.eq(dataType, entry.getKey(), true)) {
        return entry.getValue();
      }
    }

    throw new DBException(StringUtil.format("未注册类型 {}", dataType));
  }

}
