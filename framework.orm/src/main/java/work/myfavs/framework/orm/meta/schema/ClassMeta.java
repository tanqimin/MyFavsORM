package work.myfavs.framework.orm.meta.schema;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.ReflectUtil;

@Data
public class ClassMeta {

  private String         className;
  private String         tableName;
  private GenerationType strategy;

  private AttributeMeta       primaryKey;
  private List<AttributeMeta> updateAttributes = new LinkedList<>();
  private List<AttributeMeta> queryAttributes  = new LinkedList<>();

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

    Table     table     = clazz.getAnnotation(Table.class);
    ClassMeta classMeta = new ClassMeta();

    if (table != null) {
      classMeta.setClassName(clazz.getName());
      classMeta.setStrategy(table.strategy());
      classMeta.setTableName(table.value().isEmpty()
                                 ? clazz.getSimpleName()
                                 : table.value());
    }

    List<Field> fields = ReflectUtil.fieldsOf(clazz, Column.class);

    for (Field field : fields) {
      AttributeMeta attributeMeta = AttributeMeta.createInstance(field);
      classMeta.queryAttributes.add(attributeMeta);
      if (attributeMeta.isPrimaryKey()) {
        classMeta.setPrimaryKey(attributeMeta);
        continue;
      }
      classMeta.updateAttributes.add(attributeMeta);
    }

    return classMeta;
  }

}
