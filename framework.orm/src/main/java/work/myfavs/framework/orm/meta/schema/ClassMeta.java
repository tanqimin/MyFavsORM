package work.myfavs.framework.orm.meta.schema;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.ReflectUtil;
import work.myfavs.framework.orm.util.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 类元数据
 */
@Data
public class ClassMeta {

  private String         className;
  private String         tableName;
  private GenerationType strategy;

  private AttributeMeta              primaryKey;
  private List<AttributeMeta>        updateAttributes = new LinkedList<>();
  private Map<String, AttributeMeta> queryAttributes  = new HashMap<>();

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
      classMeta.queryAttributes.put(attributeMeta.getColumnName().toUpperCase(), attributeMeta);
      if (attributeMeta.isReadonly()) {
        continue;
      }
      if (attributeMeta.isPrimaryKey()) {
        classMeta.setPrimaryKey(attributeMeta);
        continue;
      }
      classMeta.updateAttributes.add(attributeMeta);
    }

    return classMeta;
  }

  /**
   * 检查主键，如果不为 null，则返回主键
   *
   * @return 主键
   */
  public AttributeMeta checkPrimaryKey() {

    if (primaryKey == null) {
      throw new DBException(StringUtil.format("The view class [{}] could not contain primary key", getClassName()));
    }
    return primaryKey;
  }

}
