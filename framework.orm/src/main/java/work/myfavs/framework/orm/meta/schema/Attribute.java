package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 数据库列元数据
 *
 * @author tanqimin
 */
@SuppressWarnings("unchecked")
public class Attribute
    implements Serializable {

  private static final long serialVersionUID = 6913045257426812101L;

  //region Attributes
  /**
   * 数据库 列名称
   */
  private String columnName;
  /**
   * 类 属性名称
   */
  private String fieldName;
  /**
   * 类 属性类型
   */
  private Class<?> fieldType;
  /**
   * 是否只读？
   */
  private boolean readonly = false;
  /**
   * 是否主键？
   */
  private boolean primaryKey = false;

  /**
   * 类型处理器
   */
  private PropertyHandler propertyHandler = null;
  //endregion

  //region Getter && Setter

  public String getColumnName() {
    return columnName;
  }

  public String getFieldName() {

    return fieldName;
  }

  public Class<?> getFieldType() {

    return fieldType;
  }


  public boolean isReadonly() {

    return readonly;
  }

  public boolean isPrimaryKey() {

    return primaryKey;
  }

  //endregion

  //region Constructor

  private Attribute() {
  }

  //endregion

  /**
   * 把指定字段解析为属性元数据
   *
   * @param field 指定字段
   * @return 属性元数据
   */
  static Attribute createInstance(Field field) {
    Attribute attribute = null;
    final Column column = field.getAnnotation(Column.class);
    if (column != null) {
      attribute = new Attribute();
      attribute.fieldName = field.getName();
      attribute.fieldType = field.getType();
      attribute.readonly = column.readonly();
      attribute.primaryKey = isPrimaryKey(field);
      attribute.columnName = StrUtil.isEmpty(column.value())
          ? StrUtil.toUnderlineCase(field.getName())
          : column.value();
      attribute.propertyHandler = PropertyHandlerFactory.getInstance(field.getType());
    }
    return attribute;
  }

  private static boolean isPrimaryKey(Field field) {
    return field.getAnnotation(PrimaryKey.class) != null;
  }

  public Object value(ResultSet rs) {

    try {
      return this.propertyHandler.convert(rs, columnName, fieldType);
    } catch (SQLException ex) {
      throw new DBException(ex);
    }
  }

}
