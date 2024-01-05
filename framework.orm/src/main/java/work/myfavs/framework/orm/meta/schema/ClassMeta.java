package work.myfavs.framework.orm.meta.schema;


import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.common.ArrayUtil;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;
import work.myfavs.framework.orm.util.reflection.ReflectUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 类元数据
 *
 * @author tanqimin
 */
public class ClassMeta {

  private static final Map<String/* className */, ClassMeta> CLASS_META_CACHE = new HashMap<>();

  // region Attributes

  /**
   * 是否实体类，实体类 {@link #getTableName()} 不为 {@code null}
   */
  private       boolean        isEntity;
  /**
   * 类型
   */
  private final Class<?>       clazz;
  /**
   * 数据表名
   */
  private       String         tableName;
  /**
   * 主键生成策略
   */
  private       GenerationType strategy;
  /**
   * 主键
   */
  private       Attribute      primaryKey;
  /**
   * 逻辑删除字段，null为不使用逻辑删除
   */
  private       Attribute      logicDelete;

  private final Constructor<?>                          modelConstructor;
  /**
   * 更新字段
   */
  private final Map<String /* columnName */, Attribute> updateAttributes = new LinkedHashMap<>();
  /**
   * 查询字段
   */
  private final Map<String /* columnName */, Attribute> queryAttributes  = new LinkedHashMap<>();

  // endregion

  // region Getter && Setter

  public Class<?> getClazz() {
    return clazz;
  }

  public String getTableName() {
    return tableName;
  }

  public GenerationType getStrategy() {
    return strategy;
  }

  public Attribute getPrimaryKey() {
    return primaryKey;
  }

  public Attribute getLogicDelete() {
    return logicDelete;
  }

  public boolean isEntity() {
    return isEntity;
  }

  public Map<String /* columnName */, Attribute> getUpdateAttributes() {
    return updateAttributes;
  }

  public Collection<Attribute> getUpdateAttributes(String[] columns) {
    if (ArrayUtil.isEmpty(columns)) return updateAttributes.values();

    List<Attribute> attributes = new ArrayList<>();
    for (String column : columns) {
      Attribute attribute = updateAttributes.get(column.toUpperCase());
      if (null == attribute) continue;
      attributes.add(attribute);
    }

    return attributes;
  }


  public Map<String /* columnName */, Attribute> getQueryAttributes() {
    return queryAttributes;
  }


  // endregion

  // region Constructor

  /**
   * 构造方法
   */
  private ClassMeta(Class<?> clazz) throws RuntimeException {
    this.clazz = clazz;

    final Table table = clazz.getAnnotation(Table.class);
    if (null != table) {
      this.isEntity = true;
      this.strategy = table.strategy();
      this.tableName = getTableName(table, clazz);
    }

    this.modelConstructor = ReflectUtil.getConstructor(clazz);

    final List<Field> fields = ReflectUtil.getFields(clazz);

    for (Field field : fields) {
      final Attribute attr = Attribute.createInstance(field);
      if (null == attr) {
        continue;
      }

      String columnName = attr.getColumnName().toUpperCase();
      this.queryAttributes.put(columnName, attr);

      if (attr.isReadonly()) {
        continue;
      }

      if (attr.isPrimaryKey()) {
        this.primaryKey = attr;
      } else if (attr.isLogicDelete()) {
        this.logicDelete = attr;
      } else {
        this.updateAttributes.put(columnName, attr);
      }
    }
  }

  private static String getTableName(Table table, Class<?> clazz) {
    return StringUtil.isEmpty(table.value())
        ? StringUtil.toUnderlineCase(clazz.getSimpleName())
        : table.value();
  }
  // endregion

  /**
   * 解析指定类为类元数据
   *
   * @param clazz 指定类
   * @return 列元数据
   */
  public static ClassMeta createInstance(Class<?> clazz) {
    String    className = clazz.getName();
    ClassMeta classMeta = CLASS_META_CACHE.get(className);
    if (null == classMeta) {
      classMeta = new ClassMeta(clazz);
      CLASS_META_CACHE.put(className, classMeta);
    }
    return classMeta;
  }

  @SuppressWarnings("unchecked")
  public <T> T createModel() {
    try {
      return (T) this.modelConstructor.newInstance();
    } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
      throw new DBException("创建实体实例时发生异常: %s", e.getMessage());
    }
  }

  /**
   * 检查主键，如果不为 null，则返回主键
   *
   * @return 主键
   */
  public Attribute checkPrimaryKey() {

    if (null == primaryKey) {
      throw new DBException("类型 %s 中没有发现使用 @PrimaryKey 注释标记的主键字段", this.clazz.getName());
    }
    return primaryKey;
  }

  /**
   * 检查主键，如果不为 null，返回主键数据库列名
   *
   * @return 主键数据库列名
   */
  public String getPrimaryKeyColumnName() {
    return checkPrimaryKey().getColumnName();
  }
}
