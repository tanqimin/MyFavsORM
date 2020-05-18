package work.myfavs.framework.orm.meta.schema;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.exception.DBException;

/**
 * 类元数据
 *
 * @author tanqimin
 */
public class ClassMeta implements Serializable {

  private static final long serialVersionUID = -540703036198571358L;

  //region Attributes

  /**
   * 类型
   */
  private Class<?> clazz;
  /**
   * 类名
   */
  private String className;
  /**
   * 数据表名
   */
  private String tableName;
  /**
   * 主键生成策略
   */
  private GenerationType strategy;
  /**
   * 主键
   */
  private Attribute primaryKey;
  /**
   * 是否启用逻辑删除？
   */
  private boolean enableLogicalDelete;
  /**
   * 逻辑删除字段（数据库字段）
   */
  private String logicalDeleteField;
  /**
   * 更新字段
   */
  private Attributes updateAttributes = new Attributes();
  /**
   * 查询字段
   */
  private Attributes queryAttributes = new Attributes();

  //endregion

  //region Getter && Setter


  public Class<?> getClazz() {
    return clazz;
  }

  public void setClazz(Class<?> clazz) {
    this.clazz = clazz;
  }

  public String getClassName() {

    return className;
  }

  public void setClassName(String className) {

    this.className = className;
  }

  public String getTableName() {
    if (StrUtil.isEmpty(this.tableName)) {
      return clazz.getSimpleName();
    }
    return this.tableName;
  }

  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

  public GenerationType getStrategy() {

    return strategy;
  }

  public void setStrategy(GenerationType strategy) {

    this.strategy = strategy;
  }

  public Attribute getPrimaryKey() {

    return primaryKey;
  }

  public void setPrimaryKey(Attribute primaryKey) {

    this.primaryKey = primaryKey;
  }

  public Attributes getUpdateAttributes() {

    return updateAttributes;
  }

  public List<Attribute> getUpdateAttributes(String[] columns) {
    return updateAttributes.getAttributes(columns);
  }

  public void setUpdateAttributes(Attributes updateAttributes) {

    this.updateAttributes = updateAttributes;
  }

  public Attributes getQueryAttributes() {

    return queryAttributes;
  }

  public void setQueryAttributes(Attributes queryAttributes) {

    this.queryAttributes = queryAttributes;
  }

  public boolean isEnableLogicalDelete() {

    return enableLogicalDelete;
  }

  public void setEnableLogicalDelete(boolean enableLogicalDelete) {

    this.enableLogicalDelete = enableLogicalDelete;
  }

  public String getLogicalDeleteField() {

    return logicalDeleteField;
  }

  public void setLogicalDeleteField(String logicalDeleteField) {
    if (StrUtil.isNotEmpty(logicalDeleteField)) {
      setEnableLogicalDelete(true);
      this.logicalDeleteField = logicalDeleteField;
    }
  }

  //endregion

  //region Constructor

  /**
   * 构造方法
   *
   * @param clazz
   */
  private ClassMeta(Class<?> clazz) {
    this.clazz = clazz;
    this.className = clazz.getName();

    final Table table = clazz.getAnnotation(Table.class);
    if (table != null) {
      this.strategy = table.strategy();
      this.tableName = table.value();
      this.enableLogicalDelete = StrUtil.isNotEmpty(table.logicalDeleteField());
      this.logicalDeleteField = table.logicalDeleteField();
    }

    final Field[] fields = ReflectUtil.getFields(clazz);

    if (fields == null || fields.length <= 0) {
      return;
    }

    for (Field field : fields) {
      final Attribute attr = Attribute.createInstance(field);
      if (attr == null) {
        continue;
      }

      this.queryAttributes.put(attr.getColumnName(), attr);

      if (attr.isReadonly()) {
        continue;
      }

      if (attr.isPrimaryKey()) {
        this.primaryKey = attr;
      } else {
        this.updateAttributes.put(attr.getColumnName(), attr);
      }
    }
  }
  //endregion

  /**
   * 解析指定类为类元数据
   *
   * @param clazz 指定类
   * @return 列元数据
   */
  public static ClassMeta createInstance(Class<?> clazz) {
    return new ClassMeta(clazz);
  }

  /**
   * 检查主键，如果不为 null，则返回主键
   *
   * @return 主键
   */
  public Attribute checkPrimaryKey() {

    if (primaryKey == null) {
      throw new DBException("The view class [{}] could not contain primary key", getClassName());
    }
    return primaryKey;
  }

  /**
   * 检查是否需要添加逻辑删除字段包含字段
   *
   * @return 如果不启用逻辑删除，返回false
   */
  public boolean needAppendLogicalDeleteField() {
    if (!enableLogicalDelete) {
      return false;
    }

    return !(queryAttributes.containsColumn(logicalDeleteField));
  }
}
