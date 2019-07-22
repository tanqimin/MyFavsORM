package work.myfavs.framework.orm.meta.schema;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.ReflectUtil;

/**
 * 类元数据
 */
@Data
public class ClassMeta {

  private String         className;
  private String         tableName;
  private GenerationType strategy;

  private AttributeMeta              primaryKey;
  private Map<String, AttributeMeta> updateAttributes = new ConcurrentHashMap<>();
  private Map<String, AttributeMeta> queryAttributes  = new ConcurrentHashMap<>();

  private ClassMeta() {

  }

  /**
   * 解析指定类为类元数据
   *
   * @param clazz 指定类
   *
   * @return 列元数据
   */
  public static ClassMeta createInstance(Class<?> clazz) {

    Table       table;
    ClassMeta   classMeta;
    List<Field> fields;

    table = clazz.getAnnotation(Table.class);
    classMeta = new ClassMeta();

    if (table != null) {
      classMeta.setClassName(clazz.getName());
      classMeta.setStrategy(table.strategy());
      classMeta.setTableName(table.value().isEmpty()
                                 ? clazz.getSimpleName()
                                 : table.value());
    }

    fields = ReflectUtil.fieldsOf(clazz, Column.class);

    for (Field field : fields) {
      AttributeMeta attributeMeta = AttributeMeta.createInstance(field);
      classMeta.queryAttributes.put(attributeMeta.getColumnName(), attributeMeta);
      if (attributeMeta.isReadonly()) {
        continue;
      }
      if (attributeMeta.isPrimaryKey()) {
        classMeta.setPrimaryKey(attributeMeta);
        continue;
      }
      classMeta.updateAttributes.put(attributeMeta.getColumnName(), attributeMeta);
    }

    return classMeta;
  }

}
