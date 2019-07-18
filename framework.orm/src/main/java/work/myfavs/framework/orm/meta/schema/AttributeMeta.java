package work.myfavs.framework.orm.meta.schema;

import java.io.Serializable;
import java.lang.reflect.Field;
import lombok.Data;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;

/**
 * 数据库列元数据
 */
@Data
public class AttributeMeta
    implements Serializable {

  /**
   * 数据库 列名称
   */
  private String   columnName;
  /**
   * 类 属性名称
   */
  private String   fieldName;
  /**
   * 类 属性类型
   */
  private Class<?> fieldType;
  /**
   * 是否只读？
   */
  private boolean  readonly   = false;
  /**
   * 是否主键？
   */
  private boolean  primaryKey = false;

  private AttributeMeta() {}

  /**
   * 把指定字段解析为属性元数据
   *
   * @param field 指定字段
   *
   * @return 属性元数据
   */
  public static AttributeMeta createInstance(Field field) {

    Column     column     = field.getAnnotation(Column.class);
    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
    String     fieldName  = field.getName();

    AttributeMeta attributeMeta = new AttributeMeta();
    attributeMeta.setFieldName(fieldName);
    attributeMeta.setFieldType(field.getType());
    attributeMeta.setReadonly(column.readonly());
    attributeMeta.setPrimaryKey(primaryKey != null);
    attributeMeta.setColumnName(column.value().isEmpty()
                                    ? fieldName
                                    : column.value());

    return attributeMeta;
  }

}
