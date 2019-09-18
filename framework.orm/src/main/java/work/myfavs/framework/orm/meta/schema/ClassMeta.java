package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 类元数据
 */
@Data
public class ClassMeta {

  private String         className;
  private String         tableName;
  private GenerationType strategy;

  private AttributeMeta              primaryKey;                                  //主键
  private List<AttributeMeta>        updateAttributes = new LinkedList<>();       //更新字段
  private Map<String, AttributeMeta> queryAttributes  = new HashMap<>();          //查询字段

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

    Table     table;
    ClassMeta classMeta;
    Field[]   fields;

    table = clazz.getAnnotation(Table.class);
    classMeta = new ClassMeta();

    if (table != null) {
      classMeta.setClassName(clazz.getName());
      classMeta.setStrategy(table.strategy());
      classMeta.setTableName(table.value().isEmpty()
                                 ? clazz.getSimpleName()
                                 : table.value());
    }

    fields = ReflectUtil.getFields(clazz);

    for (Field field : fields) {
      AttributeMeta attr = AttributeMeta.createInstance(field);
      if (attr == null) {
        continue;
      }

      final String  queryKey   = attr.getColumnName().toUpperCase();
      final boolean readonly   = attr.isReadonly();
      final boolean primaryKey = attr.isPrimaryKey();

      classMeta.queryAttributes.put(queryKey, attr);
      if (readonly) {
        continue;
      }
      if (primaryKey) {
        classMeta.setPrimaryKey(attr);
      } else {
        classMeta.updateAttributes.add(attr);
      }
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
      throw new DBException("The view class [{}] could not contain primary key", getClassName());
    }
    return primaryKey;
  }

}
