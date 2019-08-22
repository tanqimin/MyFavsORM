package work.myfavs.framework.orm.generator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Data;
import work.myfavs.framework.orm.generator.meta.SchemaColumn;
import work.myfavs.framework.orm.generator.meta.TypeDefinition;
import work.myfavs.framework.orm.generator.meta.sql.MetaSql;
import work.myfavs.framework.orm.meta.clause.Sql;
import work.myfavs.framework.orm.util.DBUtil;
import work.myfavs.framework.orm.util.ResultSetUtil;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

@Data
public class CodeGenerator {


  private GeneratorConfig generatorConfig;

  public CodeGenerator(GeneratorConfig generatorConfig) {

    this.generatorConfig = generatorConfig;
  }


  public void build() {

    Map<String, List<SchemaColumn>> columnsMap = createColumnMap();

    GeneratorTemplate generatorTemplate = new GeneratorTemplate();
    for (Entry<String, List<SchemaColumn>> entry : columnsMap.entrySet()) {
      List<SchemaColumn> columns = entry.getValue();
      if (columns == null || columns.isEmpty()) {
        continue;
      }
      SchemaColumn column = columns.get(0);

      Map<String, Object> params = new HashMap<>();
      params.put("package", generatorConfig.getEntitiesPackage());
      params.put("table", column.getTable());
      params.put("class", column.getClassName());
      params.put("columns", columns);

      String render = generatorTemplate.render("/entities.txt", params);

      System.out.println(render);
    }
  }

  private Map<String, List<SchemaColumn>> createColumnMap() {

    final String jdbcUrl  = generatorConfig.getJdbcUrl();
    final String jdbcUser = generatorConfig.getJdbcUser();
    final String jdbcPwd  = generatorConfig.getJdbcPwd();

    Map<String, List<SchemaColumn>> res  = new HashMap<>();
    Connection                      conn = null;
    PreparedStatement               ps   = null;
    ResultSet                       rs   = null;
    try {
      conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPwd);
      Sql sql = MetaSql.get(conn);
      ps = DBUtil.getPs(conn, sql.getSql().toString(), sql.getParams());
      rs = ps.executeQuery();

      while (rs.next()) {
        String       table  = ResultSetUtil.getString(rs, "table");
        SchemaColumn column = convert(rs);

        if (res.containsKey(table)) {
          res.get(table).add(column);
        } else {
          List<SchemaColumn> columns = new ArrayList<>();
          columns.add(column);
          res.put(table, columns);
        }
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

  private TypeDefinition createTypeDefinition(String dataType, String comment) {

    if (comment != null) {
      if (comment.contains("#")) {
        String className = comment.split("#")[1];
        return new TypeDefinition(className);
      }
    }

    for (Entry<String, TypeDefinition> entry : generatorConfig.getTypeMapper().entrySet()) {
      if (StringUtil.eq(dataType, entry.getKey(), true)) {
        return entry.getValue();
      }
    }

    throw new DBException(StringUtil.format("未注册类型 {}", dataType));
  }

}
