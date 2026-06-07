package work.myfavs.framework.orm.meta.schema;


import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.common.ArrayUtil;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.InvalidDataAccessException;
import work.myfavs.framework.orm.util.reflection.ReflectUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类元数据
 *
 * @author tanqimin
 */
public class ClassMeta {

  private static final Map<String/* className */, ClassMeta> CLASS_META_CACHE = new ConcurrentHashMap<>();

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

//  private final Constructor<?>                          modelConstructor;
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

  /**
   * 获取实体对应的 {@link Class}
   *
   * @return {@link Class} 实例
   */
  public Class<?> getClazz() {
    return clazz;
  }

  /**
   * 获取数据表名
   *
   * @return 数据表名
   */
  public String getTableName() {
    return tableName;
  }

  /**
   * 获取主键生成策略
   *
   * @return {@link GenerationType} 枚举值
   */
  public GenerationType getStrategy() {
    return strategy;
  }

  /**
   * 获取主键属性元数据
   *
   * @return {@link Attribute} 实例，可能为 {@code null}
   */
  public Attribute getPrimaryKey() {
    return primaryKey;
  }

  /**
   * 获取逻辑删除属性元数据
   *
   * @return {@link Attribute} 实例，可能为 {@code null}
   */
  public Attribute getLogicDelete() {
    return logicDelete;
  }

  /**
   * 判断是否为实体类
   *
   * @return 实体类返回 {@code true}
   */
  public boolean isEntity() {
    return isEntity;
  }

  /**
   * 获取所有可更新字段的 {@link Attribute} 映射
   *
   * @return 字段名到 {@link Attribute} 的映射
   */
  public Map<String /* columnName */, Attribute> getUpdateAttributes() {
    return updateAttributes;
  }

  /**
   * 根据指定列名获取可更新字段的 {@link Attribute} 集合
   *
   * @param columns 列名数组，为空则返回所有可更新字段
   * @return {@link Attribute} 集合
   */
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


  /**
   * 获取所有查询字段的 {@link Attribute} 映射
   *
   * @return 字段名到 {@link Attribute} 的映射
   */
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

//    this.modelConstructor = ReflectUtil.getConstructor(clazz);

    final List<Field> fields = ReflectUtil.getFields(clazz);

    for (Field field : fields) {

      final Attribute attr = Attribute.createInstance(field);

      if (null == attr) continue;

      String columnName = attr.getColumnName().toUpperCase();
      this.queryAttributes.put(columnName, attr);

      if (attr.isReadonly()) continue;

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
    return CLASS_META_CACHE.computeIfAbsent(clazz.getName(), key -> new ClassMeta(clazz));
  }

  /**
   * 检查主键，如果不为 null，则返回主键
   *
   * @return 主键
   */
  public Attribute checkPrimaryKey() {

    if (null == primaryKey) {
      throw new InvalidDataAccessException("类型 %s 中没有发现使用 @PrimaryKey 注释标记的主键字段", this.clazz.getName());
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
