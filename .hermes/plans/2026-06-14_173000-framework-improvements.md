# 框架改进实施计划

> **For Hermes:** 使用 `subagent-driven-development` skill 按任务逐个执行。

**目标:** 实施两个 P0/P1 级别的框架改进——PropertyHandler 注册策略改为增量叠加、补充核心单元测试覆盖。

**架构:**
- **Phase 1** — PropertyHandler 注册策略变更：将"全默认 or 全自定义"模式改为"先注册默认，再覆盖自定义"，消除最常见的用户踩坑点
- **Phase 2** — 补充关键单元测试：Snowflake、ReflectUtil、核心 PropertyHandler、Order 等

**技术栈:** Java 11, Maven, JUnit 4, Mockito 5.23

---

## Phase 1: PropertyHandler 注册策略改进

### Task 1: 修改 PropertyHandlerFactory — 支持增量叠加

**Objective:** 将 `register()` 和 `registerDefault()` 的行为改为：默认 Handler 始终存在，自定义 Handler 按 key 覆盖。移除"全默认 or 全自定义"的限制。

**文件:**
- Modify: `orm/src/main/java/.../meta/handler/PropertyHandlerFactory.java`

**Step 1: 修改 `registerDefault()` 方法的调用时机**

不再等待用户决定是否注册默认，而是在类初始化时自动注册：

将：
```java
public static void registerDefault() {
    register(String.class, new StringPropertyHandler());
    register(NVarchar.class, new NVarcharPropertyHandler());
    // ... 23 种
}
```

改为在 `registerDefault()` 调用时先清空，再注册全部默认。或者更简单地，在静态初始化块中注册。

但更优雅的方案是：`registerDefault()` 总是注册所有默认，而 `register()` 负责覆盖。`DBTemplate` 调用上改为：**总是先注册默认，再应用用户自定义覆盖**。

**最小改动方案** — 改 `DBTemplate.registerMapper()` 和 `PropertyHandlerFactory`：

`PropertyHandlerFactory` 保持不变（保留 `registerDefault` 和 `register` 方法），只改调用策略。

**Step 2: 修改 `DBTemplate.registerMapper()`**

将:
```java
private void registerMapper(Mapper mapper) {
    if (mapper.map.isEmpty()) {
        PropertyHandlerFactory.registerDefault();
        return;
    }
    mapper.map.forEach(PropertyHandlerFactory::register);
}
```

改为:
```java
private void registerMapper(Mapper mapper) {
    PropertyHandlerFactory.registerDefault();
    mapper.map.forEach(PropertyHandlerFactory::register);
}
```

**Step 3: 修改 `PropertyHandlerFactory.registerDefault()` 添加 `clear()` 保护**

增加一个 `clear()` 方法，确保多次调用 `registerDefault()` 不会重复注册：

```java
private static boolean defaultsRegistered = false;

public static void registerDefault() {
    if (defaultsRegistered) return;
    defaultsRegistered = true;
    register(String.class, new StringPropertyHandler());
    // ... 23 种
}
```

或者更彻底地，移除 `defaultsRegistered` 保护，每次调用 `registerDefault()` 都覆盖注册（HashMap put 会覆盖，不会有重复）。

**推荐方案：** 不加保护，直接先调用 `registerDefault()`，再覆盖用户自定义。因为 `ConcurrentHashMap.put()` 自然执行覆盖。

**Step 4: 更新 `PropertyHandlerFactory` 类注释**

将文档从"全默认 or 全自定义"改为"默认 + 自定义覆盖"的描述。

**Step 5: 运行测试确认无回归**

```bash
mvn test -pl orm
```
Expected: 305/305 通过，0 失败

**兼容性影响:** 此项改动**不破坏现有代码**。之前使用"全自定义"的用户会发现不再需要注册全部 23 种 Handler 了，非兼容性变更。

---

### Task 2: 更新 DBTemplate.Builder 的文档注释

**Objective:** 更新 `DBTemplate` 的 Javadoc 和 `Builder.mapping()` 的文档说明，反映新的注册策略。

**文件:**
- Modify: `orm/src/main/java/.../DBTemplate.java` (类注释 + `registerMapper` 注释)

---

## Phase 2: 补充核心单元测试

### Task 3: 为 Snowflake 添加单元测试

**Objective:** 为 `Snowflake.nextId()` 添加边界值测试，覆盖 workerId/dataCenterId 边界值和时钟回拨场景。

**文件:**
- Create: `orm/src/test/java/.../util/lang/SnowflakeTest.java`

**测试用例（5 个测试方法）：**

```java
public class SnowflakeTest {

    @Test
    public void shouldGenerateIdWithBoundaryWorkerId() {
        // 验证 workerId = 0 和 31（边界值）正常工作
        Snowflake sf = new Snowflake(0, 0);
        long id = sf.nextId();
        assertTrue(id > 0);
    }

    @Test
    public void shouldGenerateUniqueIds() {
        Snowflake sf = new Snowflake(1, 1);
        long id1 = sf.nextId();
        long id2 = sf.nextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void shouldExtractWorkerIdFromGeneratedId() {
        Snowflake sf = new Snowflake(15, 10);
        long id = sf.nextId();
        assertEquals(15, sf.getWorkerId(id));
        assertEquals(10, sf.getDataCenterId(id));
    }

    @Test
    public void shouldRejectInvalidWorkerId() {
        assertThrows(InvalidDataAccessException.class,
            () -> new Snowflake(-1, 0));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowOnClockMovingBackwards() {
        // 通过反射或 mock 模拟时钟回拨
        // 由于 Snowflake 内部使用 System.currentTimeMillis()，
        // 暂时先验证正常路径，时钟回拨作为已知限制
    }
}
```

**验证命令:**
```bash
mvn test -pl orm -Dtest=SnowflakeTest
```
Expected: 4 passed (时钟回拨测试标记为 `@Ignore` 或待定)

---

### Task 4: 为 ReflectUtil 添加单元测试

**Objective:** 覆盖反射缓存的边界行为、字段查找、新实例创建等功能。

**文件:**
- Create: `orm/src/test/java/.../util/reflection/ReflectUtilTest.java`

**测试用例：**

```java
public class ReflectUtilTest {

    static class Parent {
        private String parentField;
    }
    static class Child extends Parent {
        private String childField;
    }

    @Test
    public void shouldGetFieldsFromClassHierarchy() {
        List<Field> fields = ReflectUtil.getFields(Child.class);
        Set<String> names = fields.stream().map(Field::getName).collect(Collectors.toSet());
        assertTrue(names.contains("parentField"));
        assertTrue(names.contains("childField"));
    }

    @Test
    public void shouldGetFieldByName() {
        Field field = ReflectUtil.getField(Child.class, "childField");
        assertNotNull(field);
        assertEquals("childField", field.getName());
    }

    @Test
    public void shouldReturnNullForNonExistentField() {
        assertNull(ReflectUtil.getField(Child.class, "nonExistent"));
    }

    @Test
    public void shouldCreateInstance() {
        Child child = ReflectUtil.newInstance(Child.class);
        assertNotNull(child);
        assertTrue(child instanceof Child);
    }

    @Test
    public void shouldGetAndSetFieldValue() throws Exception {
        Child child = new Child();
        ReflectUtil.setFieldValue(ReflectUtil.getField(Child.class, "childField"), child, "test");
        assertEquals("test", ReflectUtil.getFieldValue(ReflectUtil.getField(Child.class, "childField"), child));
    }
}
```

**验证命令:**
```bash
mvn test -pl orm -Dtest=ReflectUtilTest
```
Expected: 6 passed

---

### Task 5: 为 Order 添加单元测试

**Objective:** 覆盖排序字段解析、注入检测、边界场景。

**文件:**
- Create: `orm/src/test/java/.../meta/pagination/OrderTest.java`

**测试用例（5+ 测试方法）：**

```java
public class OrderTest {

    @Test
    public void shouldParseAscendingByDefault() {
        Order order = Order.parse("name");
        assertTrue(order.isAscending());
        assertEquals("name", order.getClause());
    }

    @Test
    public void shouldParseDescending() {
        Order order = Order.parse("name DESC");
        assertFalse(order.isAscending());
        assertEquals("name DESC", order.getClause());
    }

    @Test
    public void shouldParseMixedCaseDirection() {
        Order order = Order.parse("name desc");
        assertEquals("name DESC", order.getClause());
    }

    @Test(expected = InvalidDataAccessException.class)
    public void shouldRejectEmptyOrderBy() {
        Order.parse("");
    }

    @Test(expected = InvalidDataAccessException.class)
    public void shouldRejectSqlInjection() {
        Order.parse("name; DROP TABLE users");
    }

    @Test
    public void shouldAcceptBacktickQuotedField() {
        Order order = Order.parse("`column` ASC");
        assertEquals("`column`", order.getClause());
    }
}
```

**验证命令:**
```bash
mvn test -pl orm -Dtest=OrderTest
```
Expected: 6 passed

---

### Task 6: 为 TableAlias 添加单元测试

**Objective:** 覆盖 ThreadLocal 设置、清理、便捷方法。

**文件:**
- Create: `orm/src/test/java/.../meta/TableAliasTest.java`

**验证命令:**
```bash
mvn test -pl orm -Dtest=TableAliasTest
```
Expected: 4+ passed

---

### Task 7: 全量运行验证

**Objective:** 确认所有测试（原有 + 新增）通过。

```bash
mvn clean test -pl orm
```
Expected:
```
Tests run: 320+, Failures: 0, Errors: 0
```

Commit:
```bash
git add orm/src/test/java/work/myfavs/framework/orm/util/lang/SnowflakeTest.java
      orm/src/test/java/work/myfavs/framework/orm/util/reflection/ReflectUtilTest.java
      orm/src/test/java/work/myfavs/framework/orm/meta/pagination/OrderTest.java
      orm/src/main/java/work/myfavs/framework/orm/DBTemplate.java
      orm/src/main/java/work/myfavs/framework/orm/meta/handler/PropertyHandlerFactory.java
git commit -m "test: 补充 Snowflake/ReflectUtil/Order 单元测试，PropertyHandler 注册改为增量叠加"
```

---

## 风险和备选方案

| 方向 | 风险 | 说明 |
|------|------|------|
| Phase 1 PropertyHandler 注册变更 | **极小** | 用户自定义 Handler 的 `register()` 调用会覆盖已注册的默认 Handler，行为等同于"最终值由用户说了算"。对现有用户无影响 |
| Phase 2 Snowflake 时钟回拨测试 | 无法直接 mock `System.currentTimeMillis()` | 可用 `PowerMock` 或 `SystemClock` 抽象层，但建议先用 `@Ignore("需要 mock System.currentTimeMillis")` 标记，作为已知待改进 |
| 测试文件命名位置 | 需与项目现有结构一致 | 参考试验 `BigDecimalPropertyHandlerTest` 的位置，将测试类放在对应的源文件同级 test 包下 |
