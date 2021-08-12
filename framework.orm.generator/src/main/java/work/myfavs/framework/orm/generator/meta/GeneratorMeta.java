package work.myfavs.framework.orm.generator.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class GeneratorMeta {

  /** 类型匹配，用于匹配数据库类型与JAVA类型的映射 */
  private Map<String, TypeDefinition> typeMapper = new TreeMap<>();
  /** 实体表定义 */
  private List<TableDefinition> tableDefinitions = new ArrayList<>();

  public Map<String, TypeDefinition> getTypeMapper() {

    return typeMapper;
  }

  public void setTypeMapper(Map<String, TypeDefinition> typeMapper) {

    this.typeMapper = typeMapper;
  }

  public List<TableDefinition> getTableDefinitions() {

    return tableDefinitions;
  }

  public void setTableDefinitions(List<TableDefinition> tableDefinitions) {

    this.tableDefinitions = tableDefinitions;
  }

  /**
   * 获取所有实体类名称
   *
   * @return 实体类名称集合
   */
  public Set<String> getEntities() {

    Set<String> entities = new TreeSet<>();
    for (TableDefinition tableDefinition : tableDefinitions) {
      entities.add(tableDefinition.getClassName());
    }
    return entities;
  }
}
