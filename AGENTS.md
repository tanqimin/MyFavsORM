# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## 项目非显而易见的核心约定

- **`@Column`、`@PrimaryKey`、`@LogicDelete`** 使用 `@Inherited`，子类会继承父类字段注解；但 **`@Table` 没有 `@Inherited`**，子类必须独立标注 `@Table` 才能被识别为实体
- **`Sql` 静态方法以大写字母开头**（`Sql.New()`、`Sql.Select()`、`Sql.Insert()`），因 Java 不允许静态方法与实例方法同名
- **PropertyHandler 注册是"全默认 or 全自定义"模式**：一旦通过 `.mapping()` 注册了任何自定义 Handler，所有 23 种内置 Handler 全部丢失，需自行注册全部所需类型
- **基础类型和包装类必须分开注册**（`Long.class` vs `long.class`），内部以 `clazz.getName()` 为 key 精确匹配
- **Starter 的 `repository.Query` 与核心 `orm.Query` 是完全不同的类**：前者是 Spring 抽象基类（继承 `BaseRepository`），每个方法自动 `try(Database db = ...)` 管理连接；后者是低层 JDBC 封装
- **`Database` 构造器自动调用 `open()`**（打开连接），使用时必须配对 `close()`，可用 `try-with-resources` 或 `tx()` 自动管理
- **`TableAlias` 基于 `ThreadLocal`**，用完必须 `clear()`；推荐使用 `TableAlias.runnable()` / `supplier()` 等自带 `try-finally` 清理的便捷方法
- **`JdbcConnFactory.releaseConnection()` 在关闭连接前自动 `commit()`**（若 autoCommit=false），fallback 到 `rollback()` — 理解此行为对事务控制至关重要
- **逻辑删除写入的是主键值**（不是 0/1 布尔值），因此逻辑删除字段类型必须与主键字段类型一致
- **`PageLite` 查询 `pageSize + 1` 行**做 hasNext 启发式判断，末页恰好填满时可能误判
- **`Cond.like()` 自动检测通配符**：若无 `%` 或 `_` 则降级为 `=` 条件；模糊转义字符是 `¦`（Unicode BROKEN BAR），非标准反斜杠
- **`Cond.in()` 空集合默认返回 `1 > 2`**（永假）；`Cond.between()` 单 null 参数降级为 `>=` 或 `<=`
- **Druid 是 optional 依赖**：未引入时分页 COUNT 改写、SQL AST 构建会失败
- **`spring-jdbc` 在 Starter 中也是 optional**：仅 `SpringConnFactory` 需要 `DataSourceUtils`
- **SB2/SB3/SB4 Starter 模块仅含 POM**，无 Java 源码；所有代码在 `orm-spring-boot-starter-common` 中

## 构建命令

```bash
# 全量构建（跳过测试，避免需要真实数据库）
mvn clean install -DskipTests

# 运行单个测试方法
mvn test -pl orm -Dtest=<TestClassName>#<methodName>
```

> 无 lint / format / Checkstyle / CI 配置，无需运行相关命令。

## 测试须知

- **硬编码连接 SQL Server** `192.168.8.246:1433`（库 `myfavs_master`，用户 `sa`），无法离线运行
- JUnit 4（不是 5），Mockito 5.23.0
- `@BeforeClass` 每次重建表（DROP → CREATE → INSERT 初始数据）
- 测试数据通过接口混入（`implements ISnowflakeTest, IUuidTest, ...`）
- JDBC URL 含 `sendStringParametersAsUnicode=false` + `encrypt=false`

## 代码风格（非标准约定）

- **`this.` 前缀**：所有类内部方法调用显式使用 `this.` 前缀
- **无 Lombok**：全部 getter/setter/构造器手写
- **`@SuppressWarnings("rawtypes")`**：`AbstractOrm` 和 `DBTemplate` 使用类级别抑制
- **`Constant` 使用 `interface` 模式**（不是 class）分组静态常量
- **所有 `if` / `for` / `try-catch-finally` 必须使用括号包裹**
- **版本号格式**：`1.0.0-YYMMDD-N`
- **`MANIFEST.MF`** 存在于各模块 `META-INF/` 但内容为空
- 驼峰转下划线：`customField01` → `custom_field_01`（数字前加下划线）
