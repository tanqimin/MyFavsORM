# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## 语言规则

- **全局使用简体中文**思考和回答问题，包括但不限于：代码注释、commit message、文档说明、与用户的对话交互。

## 项目结构

- **多模块 Maven 项目**（6 个模块）：`orm`（核心库）、`spring-boot-starter/orm-spring-boot-starter-common`（Spring 集成源码）、`spring-boot-starter/orm-spring-boot{2,3,4}-starter`（仅 POM，无 Java 源码）、`demos/spring-boot2-demo`（多租户参考实现）
- **无 CI/CD**：`.github/modernize/` 仅含迁移脚本，无 CI 工作流配置

## 辅助 Skill

项目提供了 `myfavs-orm-helper` Skill（位于 `.agents/skills/myfavs-orm-helper/`），用于辅助理解和开发 MyFavsORM 框架。Skill 覆盖以下能力：

- **框架架构解读**：`AbstractOrm` 组合模式、7 个内部组件职责、构造时序、修改原则
- **方言体系**：6 种 `SqlDialect` 实现（MySQL/PostgreSQL/SQL Server/Oracle/H2）、Upsert/分页策略
- **注解配置**：`@Table`/`@Column`/`@PrimaryKey`/`@LogicDelete`/`@Criteria`/`@Criterion` 的完整行为和继承特性
- **CRUD API 参考**：`Database` 生命周期、`Orm` 全部方法、`Cond` 条件构建、`Sql` 语句构建、`Record`/`PageLite`/`Page` 使用
- **Spring Boot 集成**：`SpringConnFactory`、`Repository<TModel>`、`BaseService` 事务控制
- **构建与测试**：Maven 命令、测试分类、集成测试配置、`PropertyHandler` 注册规则
- **代码导航**：20 个关键源码文件速查表、10 条从需求出发的代码路径

深 Code CLI 在识别到相关场景时会自动激活此 Skill。详细内容请直接查阅 `.agents/skills/myfavs-orm-helper/SKILL.md`。

## 架构要点

- **`AbstractOrm` 采用组合模式**：实体 CRUD 委派给 6 个内部组件（`OrmExecutor`、`OrmInserter`、`OrmUpdater`、`OrmDeleter`、`OrmSelector`、`OrmPager`）。各方言子类（`MySqlOrm` 等）通过构造器传入 `SqlDialect` 注入方言行为，不再覆写方法。修改 CRUD 行为应修改对应组件或方言实现而非子类
- **`OrmSqlBuilder` 依赖 Druid AST**：INSERT/UPDATE SQL 通过 Druid 的 `SQLInsertStatement`/`SQLUpdateStatement` 构建；未引入 Druid 时 INSERT/UPDATE 操作会失败（分页 COUNT 同样依赖 Druid）
- **`Database` 持有单个 `Query` 实例**：`createQuery()` 返回同一对象（通过 `query.createQuery()` 重置 PreparedStatement），无法同时执行多个独立查询
- **`SqlDialect` 方言接口统一封装 UPSERT/分页行为**：`MySqlDialect` 使用 `INSERT ... ON DUPLICATE KEY UPDATE`，`PostgreSqlDialect` 使用 `INSERT ... ON CONFLICT ... DO UPDATE`，`SqlServerDialect` 使用 `MERGE ... WHEN MATCHED ... WHEN NOT MATCHED`，`OracleDialect` 使用 `MERGE INTO ... USING (SELECT ... FROM DUAL)`。修改方言行为应修改对应 `SqlDialect` 实现而非 `OrmSqlBuilder` 或子类
- **`SqlServerDialect.getUpsertSql()` 的 `isIdentity` 参数**控制是否排除 PK 列：`isIdentity=true` 时 PK 不参与 INSERT/UPDATE 子句并附加 `OUTPUT INSERTED.pk`；`isIdentity=false` 时 PK 正常参与所有子句
- **`SqlServerDialect` 和 `OracleDialect` 处理无更新列边界**：当表仅有 PK 列无其他列时，自动降级为不含 `WHEN MATCHED` 子句的 MERGE

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
# 全量构建（跳过测试）
mvn clean install -DskipTests

# 仅编译核心模块
mvn compile -pl orm

# 运行纯单元测试（不依赖外部数据库）
mvn test -pl orm

# 运行单个测试方法
mvn test -pl orm -Dtest=<TestClassName>#<methodName>
```

> 无 lint / format / Checkstyle / CI 配置，无需运行相关命令。

## 测试须知

### 测试分类

- **纯单元测试**（默认）：使用 Mockito 模拟数据库，无需真实数据库连接。通过 `@Category(IntegrationTest.class)` 排除集成测试
- **集成测试**（需指定数据库）：继承 `AbstractTest` 的类，通过 `DatabaseConfigProvider` 读取连接配置

### 运行集成测试

集成测试类标注了 `@Category(IntegrationTest.class)`，默认被排除。
使用 `-P integration` profile 取消排除。

推荐使用**环境变量**方式传递数据库配置，避免跨平台转义问题：

<details>
<summary><b>Linux / macOS (Bash)</b></summary>

```bash
export DB_TYPE=mysql
export DB_URL="jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8&useSSL=false"
export DB_USER=root
export DB_PASSWORD=root
mvn test -pl orm -P integration -Dtest=DatabaseTest
```
</details>

<details>
<summary><b>Windows (PowerShell)</b></summary>

```powershell
$env:DB_TYPE = "mysql"
$env:DB_URL  = "jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8&useSSL=false"
$env:DB_USER = "root"
$env:DB_PASSWORD = "root"
mvn test -pl orm -P integration -Dtest=DatabaseTest
```
</details>

<details>
<summary><b>Windows (CMD)</b></summary>

```cmd
set DB_TYPE=mysql
set DB_URL=jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8^^^^useSSL=false
set DB_USER=root
set DB_PASSWORD=root
mvn test -pl orm -P integration -Dtest=DatabaseTest
```
</details>

> 机制：`orm/pom.xml` 中默认 profile（`default-unit-tests`，`activeByDefault=true`）
> 设置 `<excludedGroups>` 排除集成测试；`integration` profile 使用
> `<excludedGroups combine.self="override"/>` 清空排除，允许集成测试运行。

### 数据库配置优先级

```
系统属性 (db.type/db.url/db.user/db.password)
  → 环境变量 (DB_TYPE/DB_URL/DB_USER/DB_PASSWORD)
    → 默认 H2 内存数据库 (jdbc:h2:mem:myfavs_test;MODE=MYSQL)
```

### 集成测试数据

- JUnit 4（不是 5），Mockito 5.23.0
- `@BeforeClass` 每次根据数据库类型自动选择 DDL 脚本重建表
- DDL 脚本按数据库分离：`sql/{mssql,mysql,postgresql,h2}/myfavs_master.sql`
- 测试数据通过接口混入（`implements ISnowflakeTest, IUuidTest, ...`）
- 默认 H2 模式为 MODE=MYSQL（兼容 MySQL 语法）
- SQL Server DDL 使用 `GO` 分隔批处理，其他数据库使用 `;` 分隔

### 核心组件测试覆盖

| 组件 | 测试文件 | 方式 |
|------|---------|------|
| OrmExecutor | `OrmExecutorTest.java` | Mockito |
| OrmSelector | `OrmSelectorTest.java` | Mockito |
| OrmInserter | `OrmInserterTest.java` | Mockito |
| OrmUpdater | `OrmUpdaterTest.java` | Mockito |
| OrmDeleter | `OrmDeleterTest.java` | Mockito |
| OrmPager | `OrmPagerTest.java` | Mockito |
| Cond / Sql | `CondTest.java` / `SqlTest.java` | 纯内存 |
| PageStrategy | `PageStrategyTest.java` | 纯内存 |

## 代码风格（非标准约定）

- **`this.` 前缀**：所有类内部方法调用显式使用 `this.` 前缀
- **无 Lombok**：全部 getter/setter/构造器手写
- **`@SuppressWarnings("rawtypes")`**：`AbstractOrm` 和 `DBTemplate` 使用类级别抑制
- **`Constant` 使用 `interface` 模式**（不是 class）分组静态常量
- **所有 `if` / `for` / `try-catch-finally` 必须使用括号包裹**
- **版本号格式**：`1.0.0-YYMMDD-N`
- **`MANIFEST.MF`** 存在于各模块 `META-INF/` 但内容为空
- 驼峰转下划线：`customField01` → `custom_field_01`（数字前加下划线）
