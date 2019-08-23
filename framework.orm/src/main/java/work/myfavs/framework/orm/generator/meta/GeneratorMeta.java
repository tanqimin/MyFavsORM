package work.myfavs.framework.orm.generator.meta;

import java.util.*;
import lombok.Data;

@Data
public class GeneratorMeta {

  /**
   * 类型匹配，用于匹配数据库类型与JAVA类型的映射
   */
  private Map<String, TypeDefinition> typeMapper       = new TreeMap<>();
  /**
   * 实体表定义
   */
  private List<TableDefinition>       tableDefinitions = new ArrayList<>();

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
