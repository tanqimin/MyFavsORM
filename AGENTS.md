# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## 语言规则

- **全局使用简体中文**思考和回答问题，包括但不限于：代码注释、commit message、文档说明、与用户的对话交互。

## 项目结构

- **多模块 Maven 项目**（6 个模块）：`orm`（核心库）、`spring-boot-starter/orm-spring-boot-starter-common`（Spring 集成源码）、`spring-boot-starter/orm-spring-boot{2,3,4}-starter`（仅 POM，无 Java 源码）、`demos/spring-boot2-demo`（多租户参考实现）
- **无 CI/CD**：`.github/modernize/` 仅含迁移脚本
- **深度代码参考**：`CODE_WIKI.md`（1570 行，22 章）覆盖包结构、组件职责、关键路径、测试基础设施等，适合需要理解或修改框架源码的场景

## 辅助 Skill

项目提供了 `myfavs-orm-helper` Skill（位于 `.agents/skills/myfavs-orm-helper/`），在识别到架构解读、方言体系、CRUD API、注解行为、代码导航等场景时自动激活。覆盖能力包括：AbstractOrm 组合模式、7 个内部组件、6 种 SqlDialect 实现（MySQL/PostgreSQL/SQL Server/Oracle/H2）、@Table/@Column/@PrimaryKey/@LogicDelete/@Criteria/@Criterion 行为、Database/Orm/Cond/Sql/Record/PageLite/Page API 参考、Spring Boot 集成、构建与测试、20 个关键源码文件速查表。详细内容请直接查阅 `.agents/skills/myfavs-orm-helper/SKILL.md`。

## 架构要点

- **`AbstractOrm` 采用组合模式**：实体 CRUD 委派给 6 个内部组件（`OrmExecutor`、`OrmInserter`、`OrmUpdater`、`OrmDeleter`、`OrmSelector`、`OrmPager`）。各方言子类通过构造器传入 `SqlDialect` 注入方言行为，不再覆写方法。修改 CRUD 行为应修改对应组件或方言实现而非子类
- **`OrmSqlBuilder` 依赖 Druid AST**：INSERT/UPDATE SQL 通过 Druid 的 `SQLInsertStatement`/`SQLUpdateStatement` 构建；未引入 Druid 时 INSERT/UPDATE 操作会失败（分页 COUNT 同样依赖 Druid）。Druid 是 optional 依赖
- **`Database` 持有单个 `Query` 实例**：`createQuery()` 返回同一对象（通过 `query.createQuery()` 重置 PreparedStatement），无法同时执行多个独立查询
- **SqlServerDialect 和 OracleDialect 处理无更新列边界**：当表仅有 PK 列无其他列时，自动降级为不含 `WHEN MATCHED` 子句的 MERGE

## 项目非显而易见的核心约定

- **`@Column`、`@PrimaryKey`、`@LogicDelete`** 使用 `@Inherited`，子类会继承父类字段注解；但 **`@Table` 没有 `@Inherited`**，子类必须独立标注 `@Table` 才能被识别为实体
- **`Sql` 静态方法以大写字母开头**（`Sql.New()`、`Sql.Select()`、`Sql.Insert()`），因 Java 不允许静态方法与实例方法同名
- **PropertyHandler 注册改为「默认 + 自定义覆盖」模式**：框架始终先注册 12 种内置默认 Handler，用户通过 `.mapping()` 注册的自定义 Handler 按类型覆盖同名的默认 Handler。未注册的类型（如 Short、Double、Float、Byte、Blob 等）走兜底逻辑：枚举类型返回 `EnumPropertyHandler`，其他类型返回 `ObjectPropertyHandler`
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
mvn test -pl orm -Dtest=CondTest#eq

# 查看可用依赖更新
mvn versions:display-dependency-updates

# 查看可用插件更新
mvn versions:display-plugin-updates

# 变更版本号（格式：1.0.0-YYMMDD-N）
mvn versions:set -DnewVersion="1.0.0-260710-1"
```

> 无 lint / format / Checkstyle / CI 配置，无需运行相关命令。

## 测试须知

### 测试分类

- 纯单元测试已完成 **390 个**（使用 Mockito 模拟数据库），无需真实数据库连接。通过 `@Category(IntegrationTest.class)` 排除集成测试
- **集成测试**（需指定数据库）：继承 `AbstractTest` 的类，通过 `DatabaseConfigProvider` 读取连接配置

### 运行集成测试

集成测试类标注了 `@Category(IntegrationTest.class)`，默认被排除。使用 `-P integration` profile 取消排除（`orm/pom.xml` 中 `<excludedGroups combine.self="override"/>`）。

推荐使用**环境变量**方式传递数据库配置，避免跨平台转义问题：

```bash
# Linux / macOS (Bash)
export DB_TYPE=mysql
export DB_URL="jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true"
export DB_USER=root
export DB_PASSWORD=root
mvn test -pl orm -P integration -Dtest=DatabaseTest
```

```powershell
# Windows (PowerShell)
$env:DB_TYPE = "mysql"
$env:DB_URL  = "jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true"
$env:DB_USER = "root"
$env:DB_PASSWORD = "root"
mvn test -pl orm -P integration -Dtest=DatabaseTest
```

> Windows CMD 需用 `^^^^` 转义 `&`、`%%` 转义 `%`，强烈建议使用环境变量方式。

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
| OrmSqlBuilder | `OrmSqlBuilderTest.java` | Mockito |
| Cond / Sql | `CondTest.java` / `SqlTest.java` | 纯内存 |
| Snowflake | `SnowflakeTest.java` | 纯内存 |
| ReflectUtil | `ReflectUtilTest.java` | 纯内存 |
| Order | `OrderTest.java` | 纯内存 |
| TableAlias | `TableAliasTest.java` | 纯内存 |

## 代码风格（非标准约定）

- **`this.` 前缀**：所有类内部方法调用显式使用 `this.` 前缀
- **无 Lombok**：全部 getter/setter/构造器手写
- **`@SuppressWarnings("rawtypes")`**：`AbstractOrm` 和 `DBTemplate` 使用类级别抑制
- **`Constant` 使用 `interface` 模式**（不是 class）分组静态常量
- **所有 `if` / `for` / `try-catch-finally` 必须使用括号包裹**
- **版本号格式**：`1.0.0-YYMMDD-N`
- **`MANIFEST.MF`** 存在于各模块 `META-INF/` 但内容为空
- 驼峰转下划线：`customField01` → `custom_field_01`（数字前加下划线）
