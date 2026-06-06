package work.myfavs.framework.orm.meta.schema;

import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.LogicDelete;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.reflection.FieldVisitor;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * 数据库列元数据
 *
 * @author tanqimin
 */
@SuppressWarnings("unchecked")
public class Attribute implements Serializable {
  private static final long serialVersionUID = 6913045257426812101L;

  // region Attributes
  /**
   * 数据库 列名称
   */
  private final String          columnName;
  /**
   * Field访问器
   */
  private final FieldVisitor    fieldVisitor;
  /**
   * java.sql.Types 类型
   */
  private final int             sqlType;
  /**
   * 是否只读？
   */
  private final boolean         readonly;
  /**
   * 是否主键？
   */
  private final boolean         primaryKey;
  /**
   * 是否逻辑删除字段？
   */
  private final boolean         logicDelete;
  /**
   * 类型处理器
   */
  @SuppressWarnings("rawtypes")
  private final PropertyHandler propertyHandler;
  // endregion

  // region Getter && Setter

  /**
   * 获取数据库列名称
   *
   * @return 数据库列名称
   */
  public String getColumnName() {
    return columnName;
  }

  /**
   * 获取字段访问器
   *
   * @return {@link FieldVisitor} 实例
   */
  public FieldVisitor getFieldVisitor() {
    return fieldVisitor;
  }

  /**
   * 获取 SQL 数据类型
   *
   * @return {@link java.sql.Types} 类型常量
   */
  public int getSqlType() {
    return sqlType;
  }

  /**
   * 判断是否为只读字段
   *
   * @return 只读返回 {@code true}
   */
  public boolean isReadonly() {
    return readonly;
  }

  /**
   * 判断是否为主键字段
   *
   * @return 主键返回 {@code true}
   */
  public boolean isPrimaryKey() {
    return primaryKey;
  }

  /**
   * 判断是否为逻辑删除字段
   *
   * @return 逻辑删除字段返回 {@code true}
   */
  public boolean isLogicDelete() {
    return logicDelete;
  }

  // endregion

  // region Constructor

  private Attribute(Field field) {
    Column column = field.getAnnotation(Column.class);
    this.readonly = column.readonly();
    this.fieldVisitor = new FieldVisitor(field);
    this.primaryKey = isPrimaryKey(field);
    this.logicDelete = isLogicDelete(field);
    this.columnName = StringUtil.isEmpty(column.value())
        ? StringUtil.toUnderlineCase(field.getName())
        : column.value();

    this.propertyHandler = PropertyHandlerFactory.getInstance(field.getType());
    this.sqlType = this.propertyHandler.getSqlType();
  }


  // endregion

  /**
   * 把指定字段解析为属性元数据
   *
   * @param field 指定字段
   * @return 属性元数据
   */
  static Attribute createInstance(Field field) {

    if (null == field.getAnnotation(Column.class))
      return null;

    return new Attribute(field);
  }

  private static boolean isPrimaryKey(Field field) {
    return null != field.getAnnotation(PrimaryKey.class);
  }

  private static boolean isLogicDelete(Field field) {
    return null != field.getAnnotation(LogicDelete.class);
  }

  /**
   * 从 {@link ResultSet} 中读取值并设置到模型的指定字段
   *
   * @param model      模型实例
   * @param rs         {@link ResultSet} 实例
   * @param columnIndex 列索引（从 1 开始）
   * @param <TModel>   模型类型
   * @throws SQLException 数据库访问异常
   */
  public <TModel> void setValue(TModel model, ResultSet rs, int columnIndex) throws SQLException {
    Object value = this.propertyHandler.convert(rs, columnIndex, this.fieldVisitor.getType());
    this.setValue(model, value);
  }

  /**
   * 将指定值设置到模型的字段
   *
   * @param model  模型实例
   * @param value  属性值
   * @param <TModel> 模型类型
   */
  public <TModel> void setValue(TModel model, Object value) {
    this.fieldVisitor.setValue(model, value);
  }

  /**
   * 获取模型指定字段的值
   *
   * @param model  模型实例
   * @param <TModel> 模型类型
   * @return 字段值
   */
  public <TModel> Object getValue(TModel model) {
    return this.fieldVisitor.getValue(model);
  }

  /**
   * 从 {@link ResultSet} 中读取第一列作为主键值并设置到模型
   *
   * @param model  模型实例
   * @param rs     {@link ResultSet} 实例
   * @param <TModel> 模型类型
   * @throws SQLException 数据库访问异常
   */
  public <TModel> void setPrimaryKey(TModel model, ResultSet rs) throws SQLException {
    if (rs.next())
      setValue(model, rs, 1);
  }

  /**
   * 从 {@link ResultSet} 中读取主键值并设置到模型集合中的每个模型
   *
   * @param models 模型集合
   * @param rs     {@link ResultSet} 实例
   * @param <TModel> 模型类型
   * @throws SQLException 数据库访问异常
   */
  public <TModel> void setPrimaryKeys(Collection<TModel> models, ResultSet rs) throws SQLException {
    for (TModel model : models) {
      setPrimaryKey(model, rs);
    }
  }
}
