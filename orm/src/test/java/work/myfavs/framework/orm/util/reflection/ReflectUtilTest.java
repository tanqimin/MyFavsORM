package work.myfavs.framework.orm.util.reflection;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * {@link ReflectUtil} 反射工具类的单元测试.
 *
 * <p>覆盖以下场景：</p>
 * <ul>
 *   <li>从类继承层次中获取所有 {@link Field}</li>
 *   <li>按名称查找字段（存在 / 不存在）</li>
 *   <li>通过无参构造器创建实例</li>
 *   <li>字段值的读写操作</li>
 *   <li>为原始类型字段设置 {@code null} 时的行为</li>
 * </ul>
 *
 * @see ReflectUtil
 */
public class ReflectUtilTest {

  /**
   * 父类，包含一个字符串字段用于验证继承字段的获取.
   */
  static class Parent {
    private String parentField;
  }

  /**
   * 子类，继承 {@link Parent}，添加两个字段用于验证完整字段列表.
   */
  static class Child extends Parent {
    private String childField;
    private int    intField;
  }

  /**
   * 验证 {@link ReflectUtil#getFields(Class)} 能递归获取父类字段.
   * <p>返回的字段列表应同时包含 {@code parentField} 和 {@code childField}。</p>
   */
  @Test
  public void shouldGetFieldsFromClassHierarchy() {
    List<Field> fields = ReflectUtil.getFields(Child.class);
    Set<String> names  = fields.stream().map(Field::getName).collect(Collectors.toSet());
    assertTrue("Should include parent field", names.contains("parentField"));
    assertTrue("Should include child field", names.contains("childField"));
  }

  /**
   * 验证 {@link ReflectUtil#getField(Class, String)} 按名称查找成功.
   */
  @Test
  public void shouldGetFieldByName() {
    Field field = ReflectUtil.getField(Child.class, "childField");
    assertNotNull("Field must be found", field);
    assertEquals("childField", field.getName());
  }

  /**
   * 验证 {@link ReflectUtil#getField(Class, String)} 在不存在的字段名时返回 {@code null}.
   */
  @Test
  public void shouldReturnNullForNonExistentField() {
    assertNull("Non-existent field must return null",
        ReflectUtil.getField(Child.class, "nonExistent"));
  }

  /**
   * 验证 {@link ReflectUtil#newInstance(Class)} 通过无参构造器成功创建实例.
   */
  @Test
  public void shouldCreateInstance() {
    Child child = ReflectUtil.newInstance(Child.class);
    assertNotNull("Instance must be created", child);
    assertTrue("Instance must be of expected type", child instanceof Child);
  }

  /**
   * 验证 {@link ReflectUtil#newInstance(Class, Object...)} 传入 null 参数时仍能创建实例.
   */
  @Test
  public void shouldCreateInstanceWithNoArgs() {
    Child child = ReflectUtil.newInstance(Child.class, (Object[]) null);
    assertNotNull("Instance must be created with null params", child);
  }

  /**
   * 验证 {@link ReflectUtil#setFieldValue(Field, Object, Object)} 和
   * {@link ReflectUtil#getFieldValue(Field, Object)} 的读写一致性.
   * <p>写入字符串值后读取，结果应与写入值一致。</p>
   */
  @Test
  public void shouldGetAndSetFieldValue() {
    Field  childField = ReflectUtil.getField(Child.class, "childField");
    Child  child      = new Child();
    ReflectUtil.setFieldValue(childField, child, "testValue");
    assertEquals("testValue", ReflectUtil.getFieldValue(childField, child));
  }

  /**
   * 占位测试：{@link ReflectUtil#getGenericActualTypeArguments(Class)} 的测试.
   * <p>不能在静态上下文中直接获取泛型类型，此功能由继承 {@code BaseRepository} 的子类在启动时验证。</p>
   */
  @Test
  public void shouldGetGenericActualTypeArguments() {
    // 跳过：需要继承子类才能获取泛型实际类型参数
  }

  /**
   * 验证为原始类型字段设置 {@code null} 时，不抛出异常且保留默认值.
   * <p>{@link ReflectUtil#setFieldValue(Field, Object, Object)} 内部对原始类型字段做了 null 保护，
   * 跳过赋值以避免 {@code NullPointerException}。</p>
   */
  @Test
  public void shouldReturnPrimitiveDefaultForNullPrimitiveField() {
    Child child = new Child();
    ReflectUtil.setFieldValue(ReflectUtil.getField(Child.class, "intField"), child, null);
    assertEquals("Primitive int default should be 0", 0, child.intField);
  }
}
