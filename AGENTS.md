# AGENTS.md

## AI Agent 交互提示

全程使用中文进行思考和回复：

- 新增功能 → 先给出 设计说明 + API 示例 
- 修复 Bug → 先给出 复现方式 + 修复思路 
- 重构代码 → 先评估 风险与收益

## 项目概述

轻量级 Java ORM 框架，偏好手写 SQL。Maven 多模块项目（groupId: `work.myfavs.framework`）：

- `orm` — 核心库（JDBC 封装、ORM 映射、SQL 构建器），核心包路径 `work.myfavs.framework.orm`
- `spring-boot-starter/orm-spring-boot-starter-common` — Spring Boot 集成公共代码（`SimpleRepository`、`BaseRepository`、`Repository`、`BaseService`、`SpringConnFactory`）
- `spring-boot-starter/orm-spring-boot2-starter` — Spring Boot 2.x 启动器（仅 POM，依赖 `spring-jdbc` 5.x）
- `spring-boot-starter/orm-spring-boot3-starter` — Spring Boot 3.x 启动器（仅 POM，依赖 `spring-jdbc` 6.x）
- `spring-boot-starter/orm-spring-boot4-starter` — Spring Boot 4.x 启动器（仅 POM，依赖 `spring-jdbc` 7.x）
- `demos/spring-boot2-demo` — Spring Boot 2.7.18 示例应用（多租户动态数据源）

> **注意**：SB2/SB3/SB4 启动器模块仅为 POM 包装，无 Java 源码。所有 Java 代码在 `orm-spring-boot-starter-common` 中。

## 技术栈

- Java 11，JUnit 4（不是 5），Mockito 5.23.0
- 连接池：HikariCP 7.0.2（测试依赖），Druid 1.2.28（可选依赖）
- 日志：slf4j-api 2.0.18，slf4j-reload4j（测试用）
- SQL AST：Alibaba Druid（`DruidUtil` 桥梁），用于分页 SQL 改写和格式化
- 自定义函数式接口（`ThrowingConsumer`, `ThrowingFunction`, `ThrowingRunnable`, `ThrowingSupplier`）包装 SQLException
- 自定义类型 `NText` / `NVarchar`（`util.lang` 包）用于 SQL Server NText/NVarchar 列处理

## 构建命令

```bash
# 全量构建（跳过测试，避免需要真实数据库）
mvn clean install -DskipTests

# 仅编译 orm 模块
mvn compile -pl orm

# 运行 orm 全部测试（需要 SQL Server，见下方说明）
mvn test -pl orm

# 运行单个测试类
mvn test -pl orm -Dtest=<TestClassName>

# 运行单个测试方法
mvn test -pl orm -Dtest=<TestClassName>#<methodName>
```

**注意**：项目无 lint / format / typecheck / Checkstyle / CI 配置，无需运行相关命令。

## 测试须知

- 测试继承 `orm/src/test/java/work/myfavs/framework/orm/AbstractTest.java`
- `AbstractTest` 在 `@BeforeClass` 中初始化 HikariDataSource → DBTemplate → Database，并通过 `database.tx()` 执行建表 DDL（按 `GO` 分隔）
- 测试数据通过接口混合（`implements ISnowflakeTest, IUuidTest, ...`），每个接口的 `initXxx()` 方法提供静态测试数据
- **硬编码连接 SQL Server** `192.168.8.246:1433`（库 `myfavs_master`，用户 `sa`）
- JDBC URL 包含 `sendStringParametersAsUnicode=false` — 如使用其他数据库需注意调整此项
- 建表 SQL：`orm/src/test/resources/sql/mssql/myfavs_master.sql`
- `@BeforeClass` 每次会重建表（DROP → CREATE → INSERT 初始数据）
- 测试需要真实数据库连接，无法离线运行
- 无 CI 流水线，测试仅本地执行
- `orm` 模块测试依赖（`HikariCP`、`mysql-connector-j`、`mssql-jdbc`）均为 `test` 作用域
- 测试会使用 `orm.truncate()` 清空表数据，适用于所有主键策略

## 核心架构

### 生命周期链路

```
DBTemplate          ← 全局单例（Builder 模式构建，存入静态池 ConcurrentHashMap）
  │
  └─ createDatabase() → Database   ← 连接+事务范围（AutoCloseable）
       │
       ├─ createOrm()    → Orm (AbstractOrm 子类) ← 组合 7 个内部组件
       │    ├── OrmSqlBuilder    ← SQL 语句生成（Druid AST 封装）
       │    ├── OrmExecutor      ← execute() 执行体系
       │    ├── OrmInserter      ← create() 插入逻辑
       │    ├── OrmUpdater       ← update() 更新逻辑
       │    ├── OrmDeleter       ← delete() 删除逻辑
       │    ├── OrmSelector      ← find()/get() 查询逻辑
       │    └── OrmPager + PageStrategy  ← 分页查询（策略模式）
       │
       └─ createQuery()  → Query  ← JDBC 原始查询封装
```

**ConnFactory 继承体系**：

```
ConnFactory (abstract)          ← 定义 openConnection / getCurrentConnection / closeConnection
  └── JdbcConnFactory            ← 默认实现，ThreadLocal<Connection> + 嵌套深度计数器
       └── SpringConnFactory     ← 重写 createConnection/releaseConnection，委托 DataSourceUtils
```

**关键类职责**：

| 类 | 包路径 | 职责 |
|---|---|---|
| `DBTemplate` | `work.myfavs.framework.orm.DBTemplate` | 全局配置入口（Builder 模式），持有 DataSource、ConnFactory、PKGenerator、PropertyHandler 注册表。构建后自动存入静态 `POOL` |
| `Database` | `work.myfavs.framework.orm.Database` | 连接+事务范围（`AutoCloseable`）。`tx(consumer/function)` 提供事务包装 |
| `JdbcConnFactory` | `work.myfavs.framework.orm.JdbcConnFactory` | 默认连接工厂，`ThreadLocal` 嵌套复用 |
| `Query` | `work.myfavs.framework.orm.Query` | JDBC 原生查询封装 |
| `Orm` | `work.myfavs.framework.orm.orm.Orm` | 实体级 CRUD 接口（约 60 个方法） |
| `AbstractOrm` | `work.myfavs.framework.orm.orm.impl.AbstractOrm` | 组合委派基类（~380 行），持有 6 个组件引用 |
| `OrmSqlBuilder` | `orm/component` | SQL 语句生成（Druid AST），`getTableName()`/`createCondition()` 静态方法 |
| `OrmExecutor` | `orm/component` | execute() 执行体系 |
| `OrmInserter` | `orm/component` | 插入逻辑，3 种批量策略 |
| `OrmUpdater` | `orm/component` | 更新逻辑，SQL Server JDBC Batch |
| `OrmDeleter` | `orm/component` | 删除/截断，2100 参数自动切割 |
| `OrmSelector` | `orm/component` | 查询体系（find/get/count/exists） |
| `OrmPager` | `orm/component` | 分页查询 + 参数校验 |
| `PageStrategy` | `orm/strategy` | `@FunctionalInterface`，4 个方言实现 |

### DBTemplate.Builder 配置

```java
new DBTemplate.Builder()                       // 或 new DBTemplate.Builder("dsName") 指定静态池名称
    .dataSource(dataSource)                    // 必须：javax.sql.DataSource
    .config(c -> {                             // 可选：默认 new DBConfig()
        c.setDbType("mysql");                  // 默认 "mysql"，见 DbType 常量
        c.setBatchSize(200);                   // 默认 200
        c.setFetchSize(1000);                  // 默认 1000
        c.setShowSql(true);                    // 默认 false
        c.setShowResult(true);                 // 默认 false
        c.setMaxPageSize(10000);               // 默认 -1（不限制）
        c.setDefaultIsolation(isolation);       // 默认 Connection.TRANSACTION_READ_COMMITTED
        c.setWorkerId(1);                      // Snowflake worker ID
        c.setDataCenterId(1);                  // Snowflake data center ID
        // 分页字段名可自定义：pageDataField, pageCurrentField, pageSizeField 等
    })
    .connectionFactory(SpringConnFactory.class) // 可选：默认 JdbcConnFactory.class
    .mapping(m -> {                             // 可选：默认注册内置 23 种类型
        m.register(MyType.class, new MyPropertyHandler());
    })
    .build();
```

### 数据库方言分发

`OrmFactory.createOrm(database)` 根据 `dbType` 字符串创建对应实现，各子类仅需传入 `PageStrategy` 单例：

| dbType | 实现类 | 分页策略 | 特殊行为 |
|---|---|---|---|
| `"mysql"` | `MySqlOrm` | `MySqlPageStrategy`（`LIMIT offset, count`） | — |
| `"sqlserver"` | `SqlServerOrm` | `SqlServerPageStrategy`（`ROW_NUMBER() OVER ... BETWEEN`） | — |
| `"sqlserver2012"` | `SqlServer2012Orm` | `SqlServer2012PageStrategy`（`OFFSET...FETCH NEXT`） | — |
| `"postgresql"` | `PostgreSQLOrm` | 继承 MySQL 模式（`LIMIT`） | — |
| `"oracle"` | `OracleOrm` | `OraclePageStrategy`（`ROWNUM` 双层子查询） | — |
| `"h2"` | `H2Orm` | 继承 MySQL 模式（`LIMIT`） | — |
| `"h2"` | `H2Orm` | 继承 MySQL 模式（`LIMIT`） | — |

**子类不再需要覆写任何方法。** 所有方言差异通过 `PageStrategy` 接口隔离，SQL Server 的 2100 参数限制等特殊处理由组件内部自动判断：

| 操作 | 处理位置 | 方式 |
|---|---|---|
| 批量 INSERT（IDENTITY + SQL Server） | `OrmInserter.createInOutputBatch()` | `OUTPUT INSERTED` 子句 |
| 批量 UPDATE（SQL Server） | `OrmUpdater.batchUpdateSqlServer()` | JDBC `executeBatch()` |
| 批量 DELETE（SQL Server） | `OrmDeleter.deleteByIds()` | 按 1000 切割 ID 集合 |
| `"h2"` | `H2Orm` | 跟随标准模式 | — |

**DruidUtil**（`work.myfavs.framework.orm.util.common.DruidUtil`）：将框架 `dbType` 字符串映射到 Alibaba Druid 的 `DbType` 枚举，用于 `PagerUtils.count()`（Page 模式 COUNT 改写）、SQL 格式化、AST 节点构建。

## ORM 注解与元数据系统

### 注解

| 注解 | 作用域 | @Inherited | 说明 |
|---|---|---|---|
| `@Table(value="", strategy=SNOW_FLAKE)` | 类 | 否 | 标记实体。`value` 为空时表名 = 类名转下划线小写 |
| `@Column(value="", readonly=false)` | 字段 | **是** | 标记数据库列。`value` 为空时列名 = 字段名转下划线小写。仅 `@Column` 注解的字段被 ORM 识别 |
| `@PrimaryKey` | 字段 | **是** | 标记主键字段 |
| `@LogicDelete` | 字段 | **是** | 标记逻辑删除字段。删除操作时将该字段值设为主键值，而非物理删除 |
| `@Criterion(value, operator, order, group)` | 字段 | — | 可重复注解，用于 `Cond.criteria()` 自动生成查询条件 |
| `@Criteria` | 字段 | — | `@Criterion` 的容器注解 |

### 元数据解析

- `Metadata.classMeta(clazz)` — 解析视图类（不需要 `@Table`），仅提取 `@Column` 字段
- `Metadata.entityMeta(clazz)` — 解析实体类（必须有 `@Table` 注解，否则抛异常）
- 返回的 `ClassMeta` 对象被静态缓存（`HashMap`），包含：
  - `queryAttributes` — 所有 `@Column` 字段（含只读）
  - `updateAttributes` — 可写字段（排除主键和逻辑删除字段）
  - `primaryKey` / `logicDelete` — 对应的 `Attribute` 对象
- `Attribute` 持有字段的 `FieldVisitor`、列名、SQL 类型、`PropertyHandler`，提供 `getValue()`/`setValue()`/`setPrimaryKey()` 方法

### TableAlias（同构表/分表）

`TableAlias` 是 `ThreadLocal<String>` 包装器，用于运行时覆盖表名。设置后 `AbstractOrm.getTableName()` 返回别名而非注解配置的表名。**用完必须调用 `TableAlias.clear()`** 清除。

提供 `try-finally` 自动清理的便捷方法：`runnable(name, runnable)`, `consumer(name, consumer)`, `supplier(name, supplier)`, `function(name, function)`。

## PropertyHandler 类型处理器

`PropertyHandler<T>` 抽象类定义三个方法：
- `T convert(ResultSet rs, int columnIndex, Class<T> clazz)` — 从结果集读取
- `void addParameter(PreparedStatement ps, int paramIndex, T param)` — 绑定参数
- `int getSqlType()` — JDBC 类型码

### 继承体系

```
PropertyHandler<T>
  ├── StringPropertyHandler / NVarcharPropertyHandler / NTextPropertyHandler
  ├── NumberPropertyHandler<T extends Number>  ← 数值类型抽象基类
  │     ├── IntegerPropertyHandler   (int/Integer, 分 primitive 和 wrapper)
  │     ├── LongPropertyHandler      (long/Long)
  │     ├── ShortPropertyHandler     (short/Short)
  │     ├── DoublePropertyHandler    (double/Double)
  │     ├── FloatPropertyHandler     (float/Float)
  │     ├── BytePropertyHandler      (byte/Byte)
  │     ├── BooleanPropertyHandler   (boolean/Boolean)
  │     └── BigDecimalPropertyHandler
  ├── DatePropertyHandler / LocalDateTimePropertyHandler / OffsetDateTimePropertyHandler
  ├── UUIDPropertyHandler / EnumPropertyHandler
  ├── BlobPropertyHandler / ClobPropertyHandler / ByteArrayPropertyHandler
  └── ObjectPropertyHandler  ← 兜底处理器（rs.getObject / ps.setObject）
```

### 注册方式

```java
new DBTemplate.Builder()
    .mapping(m -> m
        .register(Long.class, new LongPropertyHandler())       // 包装类
        .register(long.class, new LongPropertyHandler(true))    // 基础类型
    )
    .build();
```

**基础类型和包装类必须分开注册**，因为 `PropertyHandlerFactory.getInstance(clazz)` 以 Class 全限定名（`clazz.getName()` 字符串）为 key 精确匹配。内部使用 `ConcurrentHashMap<String, PropertyHandler>`。

**关键约定**：Handler 注册遵循"全默认 or 全自定义"模式——未调用 `mapping()` 时自动注册内置 23 种类型；一旦通过 `mapping()` 注册了**任何**自定义 Handler，所有内置 Handler 均**不注册**，需手动注册全部所需类型。对于未注册的枚举类型，自动创建 `EnumPropertyHandler`；其他类型回退到 `ObjectPropertyHandler`。

## 查询模式

ORM 支持三种查询结果类型：

1. **Entity 查询**（`Orm.find(viewClass, sql, params)`）— 通过 `ClassMeta.queryAttributes` 做 `@Column` → `ResultSet` 映射，返回 `List<T>`
2. **Record 查询**（`Orm.find(Record.class, sql, params)`）— 动态 `Record`（`extends LinkedHashMap<String, Object>`），通过 `IRecord` 接口提供 `getStr()`、`getInt()`、`getLong()` 等类型化获取方法
3. **Scalar 查询**（`Orm.find(Long.class, sql, params)`）— 单列值。如果是基础类型/包装类，通过 `PropertyHandler` 读取

`DBConvert`（`util.convert` 包）是 `ResultSet` → 对象转换的核心，按上述三种路径分发。

## SQL 构建器

`Sql`（`meta.clause` 包）提供链式 SQL 构建。**注意命名约定**：`Sql` 的静态方法以大写字母开头（例如 `Sql.New()`、`Sql.create()`），因为 Java 不允许静态方法和普通方法同名。

```java
Sql sql = new Sql("SELECT * FROM user").WHERE(Cond.eq("name", "test"))
                                       .AND(Cond.gt("age", 18))
                                       .ORDER_BY("id DESC");
```

`Clause`（`meta.clause` 包）是 `Sql` 和 `Cond` 的共同抽象基类，持有 `StringBuilder sql` 和 `List<Object> params`，提供 `param()`/`params()`/`concatWithSpace()` 等基础操作。

`Cond`（条件构建器）提供静态工厂方法：`eq()`, `ne()`, `isNull()`, `isNotNull()`, `gt()`, `ge()`, `lt()`, `le()`, `like()`（含 `FuzzyMode`）, `between()`, `in()`, `notIn()`, `exists()`, `notExists()`。支持 `AND`/`OR` 链式组合和 `criteria()` 从 `@Criterion` 注解自动生成。`Cond.logicalDelete(Attribute)` 自动附加逻辑删除条件（`field = 0`）。

`Parameters`（`LinkedHashMap<Integer, Object>`）管理参数索引→值映射，通过 `PropertyHandlerFactory` 绑定到 `PreparedStatement`。`BatchParameters`（`LinkedHashMap<Integer, Parameters>`）扩展为多批次参数，支持批量操作中按 `batchSize` 拆分 `executeBatch()`。

## 分页机制

### Page vs PageLite

| 类型 | 包含字段 | COUNT 查询 | hasNext 判断 |
|---|---|---|---|
| `Page<T>` | data, currentPage, pageSize, totalPages, totalRecords | 是（通过 Druid `PagerUtils.count()`） | — |
| `PageLite<T>` | data, currentPage, pageSize, hasNext | 否 | `data.size() == pageSize + 1`（多取 1 行） |

`IPageable` 接口（`getEnablePage()`, `getCurrentPage()`, `getPageSize()`）和 `ISortable` 接口（`getOrderBy()`）允许请求对象直接控制分页与排序。

### DBConfig 分页配置

- `maxPageSize`：-1 不限制，否则作为 `pageSize` 上限强制校验
- `pageDataField` / `pageCurrentField` / `pageSizeField` / `pageTotalPageField` / `pageTotalRecordField` / `pageHasNextField`：自定义 `Page`/`PageLite` 的 JSON 序列化字段名

## Spring Boot Starter 模式

### 继承链

```
SimpleRepository                          ← 持 protected DBTemplate dbTemplate
  └── BaseRepository                      ← 只读快捷：find(), findTop(), get(), count(), findMap()
       └── Query (repository 包)           ← 分页：findPage(), findPageLite()；Record 快捷方法
            └── Repository<TModel>         ← 实体 CRUD：getById(), create(), update(), delete()
```

**注意**：Starter 的 `repository.Query` 与核心 `Query`（`work.myfavs.framework.orm.Query`）是不同的类。前者是抽象基类（继承 `BaseRepository`），后者是低层 JDBC 封装。Starter 的 `Query` 每个方法自动通过 `try (Database db = dbTemplate.createDatabase())` 管理连接。

### SpringConnFactory 事务集成

`SpringConnFactory extends JdbcConnFactory` 重写了两个关键方法：

- `createConnection()` → `DataSourceUtils.getConnection(dataSource)` — 从 Spring 事务管理器获取连接
- `releaseConnection(conn)` → `DataSourceUtils.releaseConnection(conn, dataSource)` — 归还连接（不物理关闭）

效果：在 `@Transactional` 方法中，ORM 使用与 Spring 事务**同一个** Connection，`commit()`/`rollback()` 由 Spring 统一管理。

### 无自动配置

Starter 不提供 `spring.factories`、`@Configuration` 类或 `@ConditionalOn*` 注解。消费者需手动创建 `DBTemplate` Bean：

```java
@Configuration
public class DataSourceConfig {
    @Bean
    public DBTemplate dbTemplate(DataSource dataSource) {
        return new DBTemplate.Builder()
            .dataSource(dataSource)
            .connectionFactory(SpringConnFactory.class)   // 启用 Spring 事务集成
            .config(c -> c.setDbType("mysql"))
            .mapping(m -> m.register(...))
            .build();
    }
}
```

Repository 通过 `@Qualifier("dbTemplate")` 注入 `DBTemplate`。`BaseService` 提供编程式事务方法（`tx(callback)` 等 10+ 重载，基于 `PlatformTransactionManager`），也可配合 `@Transactional` 声明式事务。

### Demo 特有配置

`demos/spring-boot2-demo` 使用 **fastjson**（排除 `spring-boot-starter-json`）做 JSON 序列化，并注册了 `NVarcharObjectSerializer`/`NVarcharObjectDeserializer`。同时提供 Jackson 备选配置（`JacksonConfig` + `NVarcharModule`）。

### 多租户模式（demo 展示）

Demo 展示的模式：`DynamicDataSource extends AbstractRoutingDataSource` + `DataSourceAspect`（AOP）读 HTTP header `tenant-name` 切换 `ThreadLocal` 数据源。非框架特性，仅作为集成参考。

### Starter 依赖

- `orm`（compile）
- `spring-jdbc`（**optional**，仅 `SpringConnFactory` 需要 `DataSourceUtils`），按 Spring Boot 版本选用：
  - SB2: `spring-jdbc` 5.3.39
  - SB3: `spring-jdbc` 6.2.18
  - SB4: `spring-jdbc` 7.0.7
- 无 Spring Boot 直接依赖，由使用方提供

## 异常体系

```
RuntimeException
  └── DBException                       ← ORM 框架数据访问异常总基类
       ├── ConnectionException          ← 连接获取、事务提交/回滚、保存点失败
       ├── DataRetrievalException       ← SQL 查询、更新、批处理、参数绑定失败
       ├── InvalidDataAccessException   ← 配置错误、类型转换、反射失败、SQL 注入等
       └── PaginationException          ← 分页参数越界（继承 InvalidDataAccessException）
```

`catch (DBException e)` 可统一捕获所有 ORM 异常。

## 主键策略

| 策略 | 说明 | 生成方式 |
|---|---|---|
| `SNOW_FLAKE`（默认） | Twitter Snowflake 算法 | `PKGenerator.nextSnowFakeId()` → `Snowflake.nextId()`（long，41+5+5+12 位布局） |
| `UUID` | UUID 字符串 | `PKGenerator.nextUUID()` → `UUID.randomUUID().toString()` |
| `IDENTITY` | 数据库自增 | `Statement.RETURN_GENERATED_KEYS` → `ps.getGeneratedKeys()` 回读 |
| `ASSIGNED` | 手动赋值 | 插入时主键为 null 则抛 `InvalidDataAccessException` |

`workerId` / `dataCenterId` 通过 `DBConfig` 配置（范围 1-30）。Snowflake 支持 2 秒时钟回拨容忍。

## Maven Central 发布

- 发布到 `oss.sonatype.org`（snapshots + staging）
- GPG 签名、source jar、javadoc jar 在 `verify` 阶段
- demo 模块通过 `central-publishing-maven-plugin` 的 `excludeArtifacts` 排除在发布之外
- 版本号格式：`1.0.0-YYMMDD-N`

## 文件风格

- 所有 getter/setter/构造器均为手写，无 Lombok 或代码生成工具依赖
- 所有公共类需编写完整 Javadoc（类注释、构造器参数说明、方法注释及参数说明）
- 测试类继承 `AbstractTest`，使用 JUnit 4 的 `@Test` 和 `Assert`
- 无 Checkstyle / Spotless / Formatter 配置，无 CI 流水线

## 关键约定

- 字段/表名默认策略：驼峰转下划线小写（`productCode` → `product_code`），含数字的字段也会分隔（`customField01` → `custom_field_01`）
- 主键策略默认 `SNOW_FLAKE`，需在 `@Table(strategy=...)` 中显式指定其他策略
- `PropertyHandler` 基础类型和包装类分开注册（以 Class 全限定名 `clazz.getName()` 为 key 精确匹配，`ConcurrentHashMap` 实现）
- `PropertyHandlerFactory` 的 Handler 注册遵循"全默认 or 全自定义"模式——注册自定义 Handler 会覆盖所有内置 Handler
- `@Column`、`@PrimaryKey`、`@LogicDelete` 使用 `@Inherited`，子类会继承父类的字段注解；`@Table` **不继承**
- 同构表（分表）操作通过 `TableAlias` 实现，推荐使用 `TableAlias.runnable(name, runnable)` / `supplier(name, supplier)` 等自带清理的方法
- 核心 ORM 模块可选依赖：`slf4j-api`、`druid`；HikariCP / MySQL Connector / MSSQL JDBC 均为 test 作用域，不会传递到下游
- `DBTemplate` 构建后自动存入静态池，可通过 `DBTemplate.get("dsName")` 按名称查找。可在 Starter 中通过 `new SimpleRepository(DBTemplate.get("dsName"))` 实现多数据源
- SQL Server 批量更新/删除操作受 2100 参数限制：`Constant.MAX_PARAM_SIZE_FOR_MSSQL = 1000` 用于切割 ID 集合
- `Sql` 静态方法以大写字母开头（`Sql.New()`）, 因 Java 限制静态方法无法与普通方法同名
