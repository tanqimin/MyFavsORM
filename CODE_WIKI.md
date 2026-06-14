# CODE_WIKI

MyFavs ORM 代码深度参考。面向需要理解、调试或修改框架源码的开发者。

---

## 目录

1. [包结构总览](#1-包结构总览)
2. [DBTemplate — 全局配置入口](#2-dbtemplate--全局配置入口)
3. [Database — 连接与事务范围](#3-database--连接与事务范围)
4. [ConnFactory 体系 — 连接管理](#4-connfactory-体系--连接管理)
5. [Query — JDBC 原生封装](#5-query--jdbc-原生封装)
6. [Orm / AbstractOrm — 实体 CRUD 引擎](#6-orm--abstractorm--实体-crud-引擎)
7. [OrmFactory — 方言分发](#7-ormfactory--方言分发)
8. [Clause / Sql / Cond — SQL 构建体系](#8-clause--sql--cond--sql-构建体系)
9. [Parameters / BatchParameters — 参数管理](#9-parameters--batchparameters--参数管理)
10. [注解系统](#10-注解系统)
11. [元数据系统](#11-元数据系统)
12. [PropertyHandler 体系](#12-propertyhandler-体系)
13. [分页机制](#13-分页机制)
14. [主键生成](#14-主键生成)
15. [DBConvert — 结果集转换核心](#15-dbconvert--结果集转换核心)
16. [异常体系](#16-异常体系)
17. [TableAlias — 运行时表名覆盖](#17-tablealias--运行时表名覆盖)
18. [工具类](#18-工具类)
19. [Spring Boot Starter 模块](#19-spring-boot-starter-模块)
20. [测试基础设施](#20-测试基础设施)
21. [DruidUtil — 阿里 Druid 桥梁](#21-druidutil--阿里-druid-桥梁)
22. [数据流与关键路径](#22-数据流与关键路径)

---

## 1. 包结构总览

```
work.myfavs.framework.orm/
│
├── DBTemplate.java          # 全局配置入口（Builder + 静态池）
├── Database.java            # 连接+事务范围（AutoCloseable）
├── Query.java               # JDBC 原生封装
├── JdbcConnFactory.java     # 默认连接工厂
├── ConnFactory.java         # 连接工厂抽象
├── DBConfig.java            # 数据库配置
│
├── orm/
│   ├── Orm.java             # 实体 CRUD 接口（~60 方法）
│   ├── OrmFactory.java      # 方言分发工厂
│   ├── impl/
│   │   ├── AbstractOrm.java # 组合委派基类（~380 行）
│   │   ├── MySqlOrm.java
│   │   ├── SqlServerOrm.java
│   │   ├── SqlServer2012Orm.java
│   │   ├── PostgreSQLOrm.java
│   │   ├── OracleOrm.java
│   │   └── H2Orm.java
│   ├── component/           # 内部组件（非继承用途）
│   │   ├── OrmSqlBuilder.java  # SQL 语句生成（Druid AST 封装）
│   │   ├── OrmExecutor.java    # 执行层（execute 方法体系）
│   │   ├── OrmInserter.java    # 插入层（单条/批量创建策略）
│   │   ├── OrmUpdater.java     # 更新层（单条/CASE WHEN/SQL Server JDBC Batch）
│   │   ├── OrmDeleter.java     # 删除层（物理/逻辑删除 + 截断）
│   │   ├── OrmSelector.java    # 查询层（find/get/findTop/findMap/count/exists）
│   │   └── OrmPager.java       # 分页层（Page/PageLite/参数校验）
│   └── strategy/            # 分页策略（可插拔）
│       ├── PageStrategy.java            # 函数式接口
│       ├── MySqlPageStrategy.java       # LIMIT offset, count
│       ├── SqlServerPageStrategy.java   # ROW_NUMBER() OVER
│       ├── SqlServer2012PageStrategy.java # OFFSET...FETCH NEXT
│       └── OraclePageStrategy.java      # ROWNUM 双层子查询
│
├── meta/
│   ├── annotation/           # 注解定义
│   │   ├── Table.java
│   │   ├── Column.java
│   │   ├── PrimaryKey.java
│   │   ├── LogicDelete.java
│   │   ├── Criterion.java
│   │   └── Criteria.java
│   ├── schema/               # 元数据
│   │   ├── ClassMeta.java
│   │   ├── Attributes.java
│   │   ├── Attribute.java
│   │   └── Metadata.java
│   ├── clause/               # SQL 构建器
│   │   ├── Clause.java       # 抽象基类
│   │   ├── Sql.java
│   │   └── Cond.java
│   ├── handler/              # 类型处理器
│   │   ├── PropertyHandler.java
│   │   └── impls/ (23 个实现)
│   ├── pagination/           # 分页
│   │   ├── Page.java, PageLite.java, PageBase.java
│   │   ├── IPageable.java, ISortable.java
│   │   ├── Order.java, PageModel.java
│   ├── enumeration/
│   │   ├── GenerationType.java
│   │   ├── FuzzyMode.java
│   │   └── Operator.java
│   ├── criteria/             # 分页请求对象
│   │   ├── PageableCriteria.java
│   │   └── SortableCriteria.java
│   ├── DbType.java           # 数据库类型常量
│   ├── Record.java           # 动态记录（LinkedHashMap）
│   ├── IRecord.java          # Record 接口
│   ├── Parameters.java       # 参数集合
│   ├── BatchParameters.java  # 批量参数集合
│   ├── SqlLog.java           # SQL 日志格式化
│   └── TableAlias.java       # 分表/运行时表名切换
│
└── util/
    ├── common/
    │   ├── Constant.java     # 框架常量
    │   ├── DruidUtil.java    # Druid AST 工具
    │   ├── StringUtil.java
    │   ├── SqlUtil.java
    │   ├── CollectionUtil.java
    │   ├── ArrayUtil.java
    │   ├── IOUtil.java
    │   └── Enumerator.java
    ├── convert/
    │   ├── DBConvert.java    # ResultSet → 对象
    │   └── ConvertUtil.java  # 类型转换工具
    ├── reflection/
    │   ├── ReflectUtil.java  # 反射工具
    │   └── FieldVisitor.java # 字段访问器（get/set 缓存）
    ├── id/
    │   └── PKGenerator.java  # 主键生成器
    ├── lang/
    │   ├── Snowflake.java    # 雪花算法
    │   ├── NText.java
    │   ├── NVarchar.java
    │   └── Unicode.java
    ├── func/
    │   ├── ThrowingConsumer.java
    │   ├── ThrowingFunction.java
    │   ├── ThrowingRunnable.java
    │   └── ThrowingSupplier.java
    └── exception/
        ├── DBException.java
        ├── ConnectionException.java
        ├── DataRetrievalException.java
        ├── InvalidDataAccessException.java
        └── PaginationException.java
```

Starter 模块 (`spring-boot-starter/orm-spring-boot-starter-common`):

```
work.myfavs.framework.orm/
├── SpringConnFactory.java    # Spring 连接工厂
├── business/
│   └── BaseService.java     # 编程式事务基类
└── repository/
    ├── SimpleRepository.java # 基类（持 DBTemplate）
    ├── BaseRepository.java   # 只读查询快捷方法
    ├── Query.java            # 分页 + Record 查询
    └── Repository.java       # 实体 CRUD
```

---

## 2. DBTemplate — 全局配置入口

**文件：** `orm/src/main/java/work/myfavs/framework/orm/DBTemplate.java`

### 职责

框架的配置根对象，持有 `DataSource`、`ConnFactory`、`PKGenerator`、`PropertyHandlerFactory` 等全局组件。构建后自动存入静态池，供后续按名称查找。

### 静态池

```java
private static final Map<String, DBTemplate> POOL = new ConcurrentHashMap<>();
```

- `DBTemplate.get("dsName")` — 按名称查找，不存在抛 `InvalidDataAccessException`
- `DBTemplate.add("dsName", dbTemplate)` — 手动加入（或覆盖），供非 Builder 方式使用
- Builder 构建后**自动调用** `add()` 存入池中

### Builder 设计

```java
public static class Builder {
    private final String   dsName;                    // 默认 "default"
    private DataSource     dataSource;                // 必须
    private DBConfig       config = new DBConfig();   // 可选，默认配置
    private Class<? extends ConnFactory> connectionFactory = JdbcConnFactory.class;
    private final Mapper   mapper = new Mapper();     // PropertyHandler 注册容器
}
```

**关键执行顺序：**

1. `DBTemplate` 构造函数调用 `createConnFactory()` — 反射实例化 `ConnFactory`（构造函数签名为 `(DataSource)`）
2. `registerMapper()` — 始终先调用 `PropertyHandlerFactory.registerDefault()` 注册 23 种内置默认 Handler，再通过 `mapper.map.forEach()` 应用用户自定义的 Handler（自定义按类型覆盖默认，**"默认 + 自定义覆盖"** 模式）
3. `build()` 最后调用 `POOL.put(dsName, this)`

### 多数据源

```java
new DBTemplate.Builder("ds_orders")   // 指定不同名称
    .dataSource(ordersDataSource)
    .config(c -> c.setDbType("mysql"))
    .build();

// 在其他地方按名称获取
DBTemplate ordersTemplate = DBTemplate.get("ds_orders");
```

---

## 3. Database — 连接与事务范围

**文件：** `orm/src/main/java/work/myfavs/framework/orm/Database.java`

### 生命周期

```
new Database(dbTemplate)   → 构造函数调用 this.open()   → 连接深度+1
database.close()            → connFactory.closeConnection() → 连接深度-1（归零才物理关闭）
```

### open()/close() 配对

`open()` 和 `close()` 是显式配对方法，用于嵌套场景：

```java
database.open();   // 深度 1
database.open();   // 深度 2
database.close();  // 深度 1（不物理关闭）
database.close();  // 深度 0（物理关闭）
```

### tx() 事务包装

有 4 个重载，核心实现逻辑：

```java
public <TResult> TResult tx(ThrowingFunction<Orm, TResult, SQLException> func, Runnable callback) {
    try (Database database = this.open()) {   // 嵌套 open
        Orm     orm    = database.createOrm();
        TResult result = func.apply(orm);
        database.commit();                    // 业务成功 → 提交
        return result;
    } catch (SQLException e) {
        this.rollback();                      // 异常 → 回滚
        throw new ConnectionException(e, ...);
    } finally {
        callback.run();                       // 后置回调
    }
}
```

**重要实现细节：**

- `this.open()` 返回一个新的 `Database` 引用（`try-with-resources`），`close()` 时连接深度-1
- 如果在外层已 `open()`，内层 `tx()` 不会物理关闭连接
- `commit()` 内部有安全判断：`autoCommit` 为 true 或连接已关闭时跳过
- `tx()` 中不要再嵌套调用 `tx()`（不会报错但语义冗余）

### Query 缓存

`Database` 持有单个 `Query` 字段实例：

```java
private Query query;

public Query createQuery(String sql, boolean autoGeneratedPK) {
    if (null == this.query) {
        return this.query = new Query(this, sql, autoGeneratedPK);
    }
    return this.query.createQuery(sql, autoGeneratedPK);
}
```

**注意：** 一个 `Database` 同时只能用一个 `Query`，复用实例通过 `query.createQuery()` 重置 PreparedStatement。

---

## 4. ConnFactory 体系 — 连接管理

### ConnFactory（抽象基类）

```java
public abstract class ConnFactory {
    protected final DataSource dataSource;

    public abstract Connection openConnection();
    public abstract Connection getCurrentConnection();
    public abstract void closeConnection(Connection connection);
}
```

### JdbcConnFactory（默认实现）

使用 `ThreadLocal<Connection>` + `ThreadLocal<Integer>` 管理嵌套连接：

```
openConnection():
  conn = getCurrentConnection()
  if conn == null → 创建新连接，深度设为 1
  else → 深度 +1
  return conn

closeConnection(conn):
  deep = connectionDeepHolder.get()
  if deep > 1 → deep -1，不关闭
  if deep == 1 → 物理关闭，clear ThreadLocal
```

**物理连接创建：** `createConnection()` → `DataSource.getConnection()` → `connection.setAutoCommit(false)`

### SpringConnFactory（Starter 模块）

重写两个关键方法：

```java
// 代替 DataSource.getConnection()
protected Connection createConnection() {
    return DataSourceUtils.getConnection(super.dataSource);
}

// 代替 connection.close()
protected void releaseConnection(Connection conn) {
    DataSourceUtils.releaseConnection(conn, super.dataSource);
}
```

**效果：** Spring `@Transactional` 中，`DataSourceUtils.getConnection()` 返回与事务绑定的同一个连接，事务由 Spring 统一提交/回滚。

---

## 5. Query — JDBC 原生封装

**文件：** `orm/src/main/java/work/myfavs/framework/orm/Query.java`

### 生命周期

```
new Query(db, sql, autoPK)
  → createQuery(sql, autoPK)     // 记录 SQL，不创建 PreparedStatement（懒创建）
  → 首次执行时 getPreparedStatement()
     → connection.prepareStatement(sql, 或 RETURN_GENERATED_KEYS)
  → close()                       // 关闭 PreparedStatement
```

### 参数绑定

参数通过 `Parameters`（`LinkedHashMap<Integer, Object>`）管理。绑定到 `PreparedStatement` 时通过 `PropertyHandlerFactory` 查找对应 Handler：

```java
private void bindParameters(PreparedStatement ps) {
    for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
        int  index = entry.getKey();
        Object  val = entry.getValue();
        PropertyHandler handler = PropertyHandlerFactory.getInstance(val.getClass());
        handler.addParameter(ps, index, val);
    }
}
```

### 批量操作

```java
addParameters(params).addBatch();   // 写入当前参数批次到 BatchParameters
executeBatch();                     // 按 batchSize 分组执行 executeBatch()
```

`BatchParameters` 是 `LinkedHashMap<Integer, Parameters>`，每个 entry 是一批参数。`executeBatch()` 按 `batchSize` 拆分：

```java
while (batchParameters.size() > batchSize) {
    flushBatch();  // 执行前 batchSize 条
}
```

### 结果集读取

`find(Class<T>)` 通过 `DBConvert.convert(rs, clazz)` 分发：

- `Record.class` → `Record`（`LinkedHashMap<String, Object>`）
- 基础类型/包装类 → `PropertyHandler` 读取
- 其他（实体类）→ `ClassMeta` 字段映射

### 关键字段

```java
private PreparedStatement preparedStatement;
private String sql;
private boolean autoGeneratedPK;      // 控制 Statement.RETURN_GENERATED_KEYS
private boolean alreadySetFetchSize;  // fetchSize 只设置一次
```

---

## 6. Orm / AbstractOrm — 实体 CRUD 引擎

**Orm 接口：** `orm/src/main/java/work/myfavs/framework/orm/orm/Orm.java`（~60 方法声明）

**AbstractOrm：** `orm/src/main/java/work/myfavs/framework/orm/orm/impl/AbstractOrm.java`（~380 行）

### 架构概览

重构后的 `AbstractOrm` 采用**组合模式**，将所有业务委派给 6 个内部组件：

```java
public abstract class AbstractOrm implements Orm {
    protected final OrmExecutor executor;   // SQL 执行
    protected final OrmInserter inserter;   // 实体创建
    protected final OrmUpdater updater;     // 实体更新
    protected final OrmDeleter deleter;     // 实体删除
    protected final OrmSelector selector;   // 实体查询
    protected final OrmPager pager;         // 分页查询

    public AbstractOrm(Database database, SqlDialect dialect) {
        OrmSqlBuilder sqlBuilder = new OrmSqlBuilder(dialect);
        this.executor = new OrmExecutor(database);
        this.inserter = new OrmInserter(database, dbTemplate, sqlBuilder, executor);
        this.updater  = new OrmUpdater(database, sqlBuilder, executor, inserter);
        this.deleter  = new OrmDeleter(database, sqlBuilder, executor);
        this.selector = new OrmSelector(database, sqlBuilder);
        this.pager    = new OrmPager(selector, sqlBuilder, dbConfig, dialect);
    }
}
```

**子类职责简化：** 过去子类需实现 `dbType()` 和 `selectPage()` 抽象方法；现在仅需在构造器中传入对应的 `SqlDialect` 单例：

```java
public class MySqlOrm extends AbstractOrm {
    public MySqlOrm(Database database) {
        super(database, MySqlDialect.INSTANCE);
    }
}
```

### CRUD 核心路径

#### 查询 (find/get)

```
find(class, sql)
  → OrmSelector.find()
    → DBConvert.convert(rs, clazz)    # 三种路径分发
```

#### 插入 (create)

```
create(modelClass, entity)
  → OrmInserter.create()
    → 获取 entityMeta
    → 检查主键字段值，为空则按 strategy 生成
    → 构建 INSERT SQL（Druid SQLInsertStatement）
    → 如果 IDENTITY → 执行后 getGeneratedKeys() 回读主键
    → 否则 → 直接 execute()
```

**批量插入策略（OrmInserter.create(Collection)）：**

| 条件 | 策略 | 方法 |
|---|---|---|
| IDENTITY + SQL Server | `OUTPUT INSERTED` 多行 INSERT | `createInOutputBatch()` |
| IDENTITY + 其他 | JDBC `executeBatch()` + `getGeneratedKeys()` | `createInJdbcBatch()` |
| 非自增（SNOW_FLAKE/UUID/ASSIGNED） | 多行 VALUES 批量 INSERT | `createInSqlBatch()` |

#### 更新 (update)

```
update(modelClass, entity)
  → OrmUpdater.update()
    → OrmSqlBuilder 构建 UPDATE SET col=? WHERE pk=?
    → 批量时自动选择策略：
      · SQL Server → JDBC executeBatch()（避免 2100 参数限制）
      · 其他 → CASE WHEN 多行 UPDATE
```

#### 删除 (delete)

```
delete(entity)
  → OrmDeleter.delete()
    → 若 entityMeta 有逻辑删除字段 → 执行 UPDATE 将逻辑删除字段置为主键值
    → 否则 → 执行 DELETE SQL（条件：主键 = ?）
  → deleteByIds(Collection) 自动处理 SQL Server 2100 参数切割
```

### 分页编排

`findPage` / `findPageLite` 在 `OrmPager` 中实现：

```
findPage(class, sql, enablePage, currentPage, pageSize):
  → 校验 maxPageSize（DBConfig）
  → DruidUtil.count(sql) 生成 COUNT 语句
  → OrmSelector 执行 COUNT 查询
  → pageStrategy.apply(sql, params, currentPage, pageSize) 生成分页 SQL
  → OrmSelector 执行分页查询
  → 返回 Page<T>
```

`PageStrategy` 是 `@FunctionalInterface`，各方言实现类提供不同分页语法：
- `MySqlPageStrategy` → `LIMIT offset, count`
- `SqlServerPageStrategy` → `ROW_NUMBER() OVER (...) BETWEEN`
- `SqlServer2012PageStrategy` → `OFFSET ... FETCH NEXT`
- `OraclePageStrategy` → `ROWNUM` 双层子查询

### SQL Server 特殊处理

统一封装在组件内部，不再通过子类重写：

| 操作 | 问题 | 处理位置 | 方式 |
|---|---|---|---|
| 批量 INSERT（IDENTITY） | SQL Server JDBC `getGeneratedKeys()` 存在已知 bug | `OrmInserter.createInOutputBatch()` | `OUTPUT INSERTED` 子句 |
| 批量 UPDATE | 2100 参数限制 | `OrmUpdater.batchUpdateSqlServer()` | JDBC `executeBatch()` + `addBatch()` |
| 批量 DELETE | 2100 参数限制（`IN` 子句） | `OrmDeleter.deleteByIds()` | 按 `Constant.MAX_PARAM_SIZE_FOR_MSSQL (1000)` 切割 ID 集合 |

### 组件职责总览

| 组件 | 包路径 | 行数 | 依赖 |
|---|---|---|---|
| `OrmSqlBuilder` | `component` | ~297 | Druid AST, `SqlDialect`, `TableAlias`, `ClassMeta` |
| `OrmExecutor` | `component` | ~150 | `Database`, `Query` |
| `OrmInserter` | `component` | ~285 | `Database`, `DBTemplate`, `OrmSqlBuilder`, `OrmExecutor` |
| `OrmUpdater` | `component` | ~415 | `Database`, `OrmSqlBuilder`, `OrmExecutor`, `OrmInserter` |
| `OrmDeleter` | `component` | ~120 | `Database`, `OrmSqlBuilder`, `OrmExecutor` |
| `OrmSelector` | `component` | ~245 | `Database`, `OrmSqlBuilder` |
| `OrmPager` | `component` | ~145 | `OrmSelector`, `OrmSqlBuilder`, `DBConfig`, `SqlDialect` |

### 主键生成

```java
private Object generatePrimaryKey(ClassMeta entityMeta, Object entity) {
    Attribute primaryKey = entityMeta.checkPrimaryKey();
    Object pkVal = primaryKey.getFieldVisitor().getValue(entity);
    if (pkVal != null) return pkVal;  // 已有值不覆盖

    switch (strategy) {
        case SNOW_FLAKE → pkGenerator.nextSnowFakeId();
        case UUID       → pkGenerator.nextUUID();
        case IDENTITY   → null（数据库生成）
        case ASSIGNED   → throw InvalidDataAccessException
    }
    primaryKey.setValue(entity, pkVal);
}
```

---

## 7. OrmFactory — 方言分发

**文件：** `orm/src/main/java/work/myfavs/framework/orm/orm/OrmFactory.java`

```java
public static Orm createOrm(Database database) {
    switch (database.getDbConfig().getDbType()) {
        case "sqlserver"      → new SqlServerOrm(database);
        case "sqlserver2012"  → new SqlServer2012Orm(database);
        case "mysql"          → new MySqlOrm(database);
        case "postgresql"     → new PostgreSQLOrm(database);
        case "oracle"         → new OracleOrm(database);
        case "h2"             → new H2Orm(database);
        default               → throw InvalidDataAccessException
    }
}
```

### 各方言实现差异

| dbType | 类 | 分页策略 | 覆盖的方法 |
|---|---|---|---|
| `mysql` | `MySqlOrm` | `MySqlPageStrategy`（`LIMIT offset, count`） | — |
| `sqlserver` | `SqlServerOrm` | `SqlServerPageStrategy`（`ROW_NUMBER() OVER ... BETWEEN`） | — |
| `sqlserver2012` | `SqlServer2012Orm` | `SqlServer2012PageStrategy`（`OFFSET ... FETCH NEXT`） | — |
| `postgresql` | `PostgreSQLOrm` | 继承 MySQL 模式（`LIMIT`） | — |
| `oracle` | `OracleOrm` | `OraclePageStrategy`（`ROWNUM` 双层子查询） | — |
| `h2` | `H2Orm` | 继承 MySQL 模式（`LIMIT`） | — |

**子类不再需要覆写任何方法。** 所有方言差异通过 `PageStrategy` 接口隔离，SQL Server 的 2100 参数限制等特殊处理由组件内部（`OrmInserter.createInOutputBatch()`、`OrmUpdater.batchUpdateSqlServer()`、`OrmDeleter.deleteByIds()`）自动判断。

---

## 8. Clause / Sql / Cond — SQL 构建体系

### Clause（抽象基类）

**文件：** `orm/src/main/java/work/myfavs/framework/orm/meta/clause/Clause.java`

```java
public abstract class Clause implements Serializable {
    protected StringBuilder sql;        // SQL 文本
    protected final List<Object> params; // 参数列表（有序）

    // 核心方法
    protected final Clause param(Object p);      // 追加单个参数
    protected final Clause params(Collection<?>); // 追加多个参数
    protected final Clause concatWithSpace(CharSequence); // 空格+追加SQL
}
```

**参数存储方式：** `List<Object>` 保持顺序。这是 `Sql` 和 `Cond` 参数传递的统一容器。

### Sql

**文件：** `orm/src/main/java/work/myfavs/framework/orm/meta/clause/Sql.java`

**命名约定：** 所有静态工厂方法以大写字母开头（`Sql.New()`、`Sql.create()`），因 Java 不允许静态方法与实例方法同名。

```java
// 实例方法（链式）
sql.WHERE(cond)        → 拼接 "WHERE ..."
sql.AND(cond)          → 拼接 "AND ..."（自动加括号）
sql.OR(cond)           → 拼接 "OR ..."
sql.ORDER_BY(cols)     → 拼接 "ORDER BY ..."
sql.GROUP_BY(cols)     → 拼接 "GROUP BY ..."
sql.HAVING(cond)       → 拼接 "HAVING ..."
sql.append(sqlText)    → 直接追加 SQL 文本
sql.append(sqlText, param) → 追加 SQL+参数
```

**方法链实现：** 每个方法返回 `this`。`WHERE()` 等检查已有子句避免重复。

### Cond

**文件：** `orm/src/main/java/work/myfavs/framework/orm/meta/clause/Cond.java`

**构造方式：** 私有构造器，通过静态工厂方法创建。

**Null 安全：** 所有条件方法支持参数为 `null` 时忽略（返回空 `Cond`），通过 `ignoreNull` 参数控制：

```java
public static Cond eq(String field, Object param) {
    return eq(field, param, true);  // 默认 null 时忽略
}

public static Cond eq(String field, Object param, boolean ignoreNull) {
    if (ignoreNull && null == param) return create();  // 返回空 Cond
    return create().append(String.format("%s = ?", field), param);
}
```

**`and()` / `or()` 实现：**

```java
public Cond and(Cond cond) {
    if (cond.notBlank()) {
        this.append("AND").append("(").append(cond).append(")");
    }
    return this;
}
```

**`criteria()` 自动条件生成：**

从 `@Criterion` 注解读取 `field`、`operator`、`order`、`group`，按 `group` 分组生成多个 `Cond`：

```java
public static Cond[] criteria(Object obj) {
    // 遍历所有 @Criterion 注解
    // 按 group 分组（默认空组）
    // 每个字段按 operator 调用对应方法
    // 返回 Cond 数组
}
```

**`logicalDelete(Attribute)`：** 生成 `field = 0` 条件，用于附带逻辑删除过滤的查询。

---

## 9. Parameters / BatchParameters — 参数管理

### Parameters

```java
public class Parameters extends LinkedHashMap<Integer, Object> {
    // Integer: 参数索引（从 1 开始）
    // Object:  参数值
}
```

### BatchParameters

```java
public class BatchParameters extends LinkedHashMap<Integer, Parameters> {
    // Integer: 批次索引
    // Parameters: 该批次的所有参数
}
```

**批量执行流程：**

```
Query.addParameters(params).addBatch()
  → 当前 parameters 快照存入 batchParameters
  → 清空 parameters

Query.executeBatch()
  → 若 batchParameters.size() > batchSize → flushBatch()
  → 遍历 batchParameters，每组执行 ps.addBatch()
  → 每 batchSize 组执行一次 ps.executeBatch()
  → 清空 batchParameters
```

---

## 10. 注解系统

**包：** `orm/src/main/java/work/myfavs/framework/orm/meta/annotation/`

### @Table

```java
@Target(TYPE)
@Retention(RUNTIME)
public @interface Table {
    String value() default "";           // 表名，默认取类名转下划线
    GenerationType strategy() default SNOW_FLAKE;  // 主键策略
}
```

**不继承**（无 `@Inherited`），子类需独立标注或通过 `ClassMeta` 手动处理。

### @Column

```java
@Inherited
@Target(FIELD)
@Retention(RUNTIME)
public @interface Column {
    String value() default "";           // 列名，默认取字段名转下划线
    boolean readonly() default false;    // 只读（不参与 INSERT/UPDATE）
}
```

**继承**（`@Inherited`），子类字段自动继承父类列的映射。

### @PrimaryKey

```java
@Inherited
@Target(FIELD)
@Retention(RUNTIME)
public @interface PrimaryKey {}
```

### @LogicDelete

```java
@Inherited
@Target(FIELD)
@Retention(RUNTIME)
public @interface LogicDelete {}
```

**约束：** 逻辑删除字段类型需与主键字段一致（删除时写入主键值）。

### @Criterion / @Criteria

```java
@Repeatable(Criteria.class)
public @interface Criterion {
    String value() default "";           // 字段名
    Operator operator() default EQ;      // 操作符
    int order() default 0;               // 条件排序
    Class<?> group() default void.class; // 分组
}
```

`operator` 支持：`EQ`, `NOT_EQUALS`, `GT`, `GE`, `LT`, `LE`, `LIKE`, `IS_NULL`, `IS_NOT_NULL`, `BETWEEN`, `IN`, `NOT_IN`。

---

## 11. 元数据系统

**包：** `orm/src/main/java/work/myfavs/framework/orm/meta/schema/`

### Metadata（入口）

```java
public static ClassMeta classMeta(Class<?> clazz) {
    return ClassMeta.createInstance(clazz);  // 视图类，不需要 @Table
}

public static ClassMeta entityMeta(Class<?> clazz) {
    ClassMeta cm = classMeta(clazz);
    if (!cm.isEntity()) throw InvalidDataAccessException("需要 @Table 注解");
    return cm;
}
```

### ClassMeta

**创建流程：**

```
ClassMeta.createInstance(clazz)
  → 检查缓存（static HashMap）
  → 解析 @Table → tableName, isEntity
  → 遍历所有字段（含继承）
     → 检查 @Column → 创建 Attribute
     → 检查 @PrimaryKey → 标记主键
     → 检查 @LogicDelete → 标记逻辑删除
  → 构建 queryAttributes / updateAttributes（排除了主键和逻辑删除）
  → 存入缓存
```

**关键字段：**

```java
public class ClassMeta {
    private String tableName;                             // 表名
    private boolean entity;                               // 是否有 @Table
    private GenerationType generationType;                // 主键策略
    private List<Attribute> queryAttributes;              // 所有列（含只读）
    private List<Attribute> updateAttributes;             // 可写列
    private Attribute primaryKey;                         // 主键
    private Attribute logicDelete;                        // 逻辑删除
}
```

**表名/列名解析规则（`StringUtil.toolClassName(String)`）：**

- 驼峰转下划线小写：`productCode` → `product_code`
- 数字前也加下划线：`customField01` → `custom_field_01`

### Attribute

```java
public class Attribute {
    private FieldVisitor fieldVisitor;      // 字段访问器（get/set 反射缓存）
    private String columnName;             // 列名
    private String sqlType;                // SQL 类型
    private PropertyHandler propertyHandler;  // 类型处理器
    private boolean primaryKey;            // 是否主键
    private boolean logicDelete;           // 是否逻辑删除
    private boolean readonly;              // 是否只读
}
```

### Attributes（工具类）

提供 `Attributes` 集合操作（`getColumnNames()`、`getUpdateAttributes(columns)` 等）。

---

## 12. PropertyHandler 体系

**包：** `orm/src/main/java/work/myfavs/framework/orm/meta/handler/`

### PropertyHandler<T> 抽象

```java
public abstract class PropertyHandler<T> {
    public abstract T convert(ResultSet rs, int columnIndex, Class<T> clazz) throws SQLException;
    public abstract void addParameter(PreparedStatement ps, int paramIndex, T param) throws SQLException;
    public abstract int getSqlType();
}
```

### PropertyHandlerFactory

```java
private static final ConcurrentHashMap<String, PropertyHandler<?>> HANDLER_MAP = ...;
private static final EnumPropertyHandler ENUM_HANDLER = new EnumPropertyHandler();
private static final ObjectPropertyHandler OBJECT_HANDLER = new ObjectPropertyHandler();
```

**键策略：** `clazz.getName()`，即全限定类名字符串。

**查找逻辑：**

```java
public static PropertyHandler getInstance(Class<?> clazz) {
    PropertyHandler<?> handler = HANDLER_MAP.get(clazz.getName());
    if (null != handler) return handler;
    return clazz.isEnum() ? ENUM_PROPERTY_HANDLER : OBJECT_PROPERTY_HANDLER;
}
```

### 数值类型处理器

`NumberPropertyHandler<T extends Number>` 是数值类型的抽象基类，子类有：

```java
IntegerPropertyHandler(boolean isPrimitive)  // true → 默认值 0
LongPropertyHandler(boolean isPrimitive)
ShortPropertyHandler(boolean isPrimitive)
DoublePropertyHandler(boolean isPrimitive)
FloatPropertyHandler(boolean isPrimitive)
BytePropertyHandler(boolean isPrimitive)
BooleanPropertyHandler(boolean isPrimitive)
BigDecimalPropertyHandler()
```

`isPrimitive` 影响 `convert()` 返回 null 时的默认值（基础类型返回 0/false，包装类返回 null）。

---

## 13. 分页机制

**包：** `orm/src/main/java/work/myfavs/framework/orm/meta/pagination/`

### PageBase

```java
public abstract class PageBase<T> {
    private List<T> data;             // 当前页数据
    private int currentPage;          // 当前页码
    private int pageSize;             // 每页大小

    // JSON 序列化字段名可自定义（DBConfig 配置）
    private String dataField          = "data";
    private String currentPageField   = "currentPage";
    private String pageSizeField      = "pageSize";
}
```

### Page<T>

```java
public class Page<T> extends PageBase<T> {
    private long totalPages;          // 总页数
    private long totalRecords;        // 总记录数
}
```

COUNT 查询使用 Druid `PagerUtils.count(sql, dbType)` 改写原始 SQL：

```java
String countSql = PagerUtils.count(sql, DruidUtil.toDbType(dbType));
```

### PageLite<T>

```java
public class PageLite<T> extends PageBase<T> {
    private boolean hasNext;          // 是否有下一页
}
```

**实现方式：** 查询时 `pageSize + 1` 行，如果实际返回 `pageSize + 1` 行则有下一页（丢弃多余行）：

```java
// AbstractOrm 中
int querySize = pageSize + 1;
// 执行分页查询
boolean hasNext = result.size() > pageSize;
if (hasNext) result.remove(result.size() - 1);
```

### IPageable / ISortable

```java
public interface IPageable {
    boolean getEnablePage();
    int getCurrentPage();
    int getPageSize();
}

public interface ISortable {
    String getOrderBy();
}
```

`PageableCriteria` / `SortableCriteria` 是 IPageable 和 ISortable 的实现类，用于构造请求对象。

---

## 14. 主键生成

**包：** `orm/src/main/java/work/myfavs/framework/orm/util/id/`

### PKGenerator

```java
public class PKGenerator {
    private final Snowflake snowflake;

    public long nextSnowFakeId() {
        return snowflake.nextId();
    }

    public String nextUUID() {
        return UUID.randomUUID().toString();
    }
}
```

### Snowflake 算法

**文件：** `orm/src/main/java/work/myfavs/framework/orm/util/lang/Snowflake.java`

**ID 布局（64-bit long）：**

```
 0 | 00000000000000000000000000000000000000000 | 00000 | 00000 | 000000000000
 0 |               41-bit timestamp             | 5-bit | 5-bit | 12-bit
   |                                            | wkr   | dc    | sequence
```

**时钟回拨容忍：** 最多 2 秒。检测到回拨时等待 2 秒重试，超时抛异常。

---

## 15. DBConvert — 结果集转换核心

**文件：** `orm/src/main/java/work/myfavs/framework/orm/util/convert/DBConvert.java`

### 三种分发路径

```java
public static <T> List<T> toList(Class<T> modelClass, ResultSet rs) {
    // 1. Record → 动态 Map
    if (modelClass == Record.class) return toRecords(modelClass, rs);
    // 2. 基础类型/包装类 → PropertyHandler 单列读取
    if (isPrimitiveOrWrapper(modelClass)) return toScalar(modelClass, rs);
    // 3. 实体类 → ClassMeta 字段映射
    return toEntities(modelClass, rs);
}
```

### Entity 映射流程

```java
toEntities(modelClass, rs):
  ClassMeta cm = Metadata.classMeta(modelClass);
  String[] columnLabels = [预读取: metaData.getColumnLabel(i).toUpperCase()]  // ① 列标签缓存
  for each row in rs:
    T instance = ReflectUtil.newInstance(modelClass)  // ② 构造器缓存 (ConcurrentHashMap)
    for i = 0..columnCount:
      Attribute attr = queryAttributes.get(columnLabels[i])
      if attr != null:
        attr.setValue(instance, rs, columnIndex)
    result.add(instance)
```

### 性能优化策略

| 优化点 | 优化方式 | 代码位置 |
|---|---|---|
| ① 列标签缓存 | 在行遍历前将 `getColumnLabel()` + `toUpperCase()` 结果预存到 `String[]` | `DBConvert.java:57-60` |
| ② 构造器缓存 | `ReflectUtil` 中通过 `ConcurrentHashMap<Class<?>, Constructor<?>>` 缓存无参构造器 | `ReflectUtil.java:31, 146-159` |

### Record 映射

```java
toRecords(modelClass, rs):
  String[] columnLabels = [预读取: metaData.getColumnLabel(i)]  // 同上列标签缓存
  for each row:
    Record rec = ReflectUtil.newInstance(modelClass)
    for i = 0..columnCount:
      rec.put(columnLabels[i], rs.getObject(columnIndex))
    result.add(rec)
```

### Scalar 映射

```java
toScalar(modelClass, rs):
  PropertyHandler handler = PropertyHandlerFactory.getInstance(modelClass);
  for each row:
    result.add(handler.convert(rs, 1, modelClass));
```

---

## 16. 异常体系

**包：** `orm/src/main/java/work/myfavs/framework/orm/util/exception/`

```
DBException (extends RuntimeException)
  ├── ConnectionException          # 连接获取、提交、回滚、保存点
  ├── DataRetrievalException       # SQL 执行、参数绑定、批处理
  ├── InvalidDataAccessException   # 配置错误、反射失败、SQL 注入检查、类型转换
  └── PaginationException          # 分页参数越界（extends InvalidDataAccessException）
```

所有异常均为 `RuntimeException`，构造函数接受 `String message, Object... args`（格式化）、`Throwable cause, String message, Object... args`。

---

## 17. TableAlias — 运行时表名覆盖

**文件：** `orm/src/main/java/work/myfavs/framework/orm/meta/TableAlias.java`

### ThreadLocal 机制

```java
private static final ThreadLocal<String> TABLE_ALIAS_POOL = new ThreadLocal<>();
```

### 使用方式

| 方法 | 说明 | 自动清理 |
|---|---|---|
| `set(name)` / `get()` / `clear()` | 手动管理 | ❌ 需调用 clear() |
| `runnable(name, task)` | 执行 Runnable | ✅ |
| `consumer(name, consumer)` | 执行 Consumer | ✅ |
| `supplier(name, supplier)` | 执行 Supplier 并返回 | ✅ |
| `function(name, function)` | 执行 Function 并返回 | ✅ |

### 在 AbstractOrm 中的调用

```java
protected static String getTableName(ClassMeta entityMeta) {
    return TableAlias.getOpt().orElse(entityMeta.getTableName());
}
```

所有 SQL 生成的入口（`insert()`, `update()`, `deleteByCond()`, `select()`, `findPage()` 等）都调用 `getTableName()` 获取表名。

---

## 18. 工具类

### StringUtil

驼峰转下划线核心逻辑：

```java
public static String toUnderline(String str) {
    // 大写字母前加下划线，数字前也加下划线
    // productCode → product_code
    // customField01 → custom_field_01
}
```

### CollectionUtil

常用方法：

```java
split(Collection, size)     // 将集合按 size 分割为多个子集合
isEmpty(Collection)          // 空判断
isNotEmpty(Collection)
toCollection(Object...)      // 变参数组转为 Collection
```

### Constant

关键常量：

```java
int MAX_PARAM_SIZE_FOR_MSSQL = 1000;     // SQL Server IN 子句最大参数数
String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss.SSS";
List<Class<?>> PRIMITIVE_TYPES = List.of(Integer, Long, Double, String, Boolean, ...);
char FUZZY_MULTIPLE = '%';               // LIKE 多字符通配符
char FUZZY_SINGLE = '_';                 // LIKE 单字符通配符
char FUZZY_ESCAPE = '¦';                 // LIKE 转义字符
```

### ReflectUtil

```java
getGenericActualTypeArguments(Class<?>)  // 获取泛型实际类型参数
getField(Class<?>, String)               // 反射获取字段（含继承，缓存 Field 映射）
setFieldValue(Object, Field, Object)     // 设置字段值
getFieldValue(Object, Field)             // 获取字段值
newInstance(Class, Object...)            // 创建实例（无参构造器通过 ConcurrentHashMap 缓存）
getConstructor(Class, Class...)          // 获取构造方法（无参构造器通过 ConcurrentHashMap 缓存）
```

### FieldVisitor

字段访问器的缓存封装，避免每次反射调用 `setAccessible`：

```java
public class FieldVisitor {
    private final Field field;
    public Object getValue(Object target);
    public void setValue(Object target, Object value);
    public void setPrimaryKey(Object target);
}
```

内部使用 `Method` 或直接 `Field.set/get`（根据是否可访问）。

### SqlUtil

**文件：** `orm/src/main/java/work/myfavs/framework/orm/util/common/SqlUtil.java`

提供 SQL 相关的工具方法，包括防 SQL 注入检查等功能。

### ConvertUtil

类型转换工具类，提供 `toUUID()`、`toString()` 等类型安全转换方法，被 `PropertyHandler` 实现类调用。

---

## 19. Spring Boot Starter 模块

### 模块结构

```
spring-boot-starter/
├── orm-spring-boot-starter-common/   # Java 源码在此
├── orm-spring-boot2-starter/         # 仅 POM，依赖 spring-jdbc 5.x
├── orm-spring-boot3-starter/         # 仅 POM，依赖 spring-jdbc 6.x
└── orm-spring-boot4-starter/         # 仅 POM，依赖 spring-jdbc 7.x
```

SB2/SB3/SB4 starter 模块**仅包含 POM 文件**，无 Java 源码。所有代码在 `common` 中。

### 继承链

```
SimpleRepository (abstract)
  └── 持 protected DBTemplate dbTemplate

BaseRepository extends SimpleRepository
  └── find(), findTop(), get(), count(), findMap() — 只读快捷方法
  └── 每个方法: try (Database db = dbTemplate.createDatabase())

Query extends BaseRepository  (注：与核心 orm.Query 不同类)
  └── findPage(), findPageLite() — 分页
  └── findRecords(), getRecord() — Record 查询

Repository<TModel> extends Query
  └── getById(), create(), update(), delete() — 实体 CRUD
  └── 通过泛型参数自动推断 modelClass
```

### BaseService

基于 `PlatformTransactionManager` 的编程式事务，提供 10+ 个重载：

```java
protected <T> T tx(TransactionCallback<T> callback);
protected <T> T tx(TransactionCallback<T> callback, int isolationLevel, int timeout, boolean readOnly);
// 无返回值版本
protected void tx(Consumer<TransactionStatus> consumer);
```

---

## 20. 测试基础设施

**包：** `orm/src/test/java/work/myfavs/framework/orm/`

### 架构概览

```
src/test/
├── java/work/myfavs/framework/orm/
│   ├── test/
│   │   ├── IntegrationTest.java          ← @Category 标记接口
│   │   └── DatabaseConfigProvider.java   ← 集中化数据库连接配置
│   ├── AbstractTest.java                 ← 集成测试基类（@Category 标记）
│   ├── DatabaseTest.java                 ← 集成测试：CRUD 全流程
│   ├── QueryTest.java                    ← 集成测试：Query 类操作
│   ├── DBTemplateTest.java               ← 集成测试：模板配置
│   ├── DBConfigTest.java                 ← 单元测试：配置对象
│   └── orm/component/
│       ├── OrmExecutorTest.java          ← 单元测试（Mockito）
│       ├── OrmSelectorTest.java          ← 单元测试（Mockito）
│       ├── OrmInserterTest.java          ← 单元测试（Mockito）
│       ├── OrmUpdaterTest.java           ← 单元测试（Mockito）
│       ├── OrmDeleterTest.java           ← 单元测试（Mockito）
│       ├── OrmPagerTest.java             ← 单元测试（Mockito）
│       └── OrmSqlBuilderTest.java        ← 单元测试（纯内存）
├── resources/sql/
│   ├── mssql/myfavs_master.sql           ← SQL Server DDL
│   ├── mysql/myfavs_master.sql           ← MySQL DDL
│   ├── postgresql/myfavs_master.sql      ← PostgreSQL DDL
│   └── h2/myfavs_master.sql             ← H2 DDL
```

### 测试分类

| 类型 | 标记 | 如何运行 | 数据库依赖 |
|------|------|---------|-----------|
| 纯单元测试 | 无 `@Category` | `mvn test` | ❌ 无（Mockito） |
| 集成测试 | `@Category(IntegrationTest.class)` | 通过 `mvn test` 但不包括 `excludedGroups` | ✅ 需要 |

**默认构建**（`mvn test`）：Surefire 通过 `excludedGroups` 排除 `IntegrationTest`，仅运行纯单元测试。

**自动排除机制**（`orm/pom.xml`）：
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <excludedGroups>work.myfavs.framework.orm.test.IntegrationTest</excludedGroups>
    </configuration>
</plugin>
```

### IntegrationTest（标记接口）

```java
public interface IntegrationTest {
    // 仅作为 JUnit @Category 的标记，无方法
}
```

标注了此接口的测试类需要真实数据库连接，被 Surefire 默认排除。

### DatabaseConfigProvider（集中化数据库配置）

```java
// 按优先级读取配置：系统属性 → 环境变量 → H2 默认值
DatabaseConfigProvider provider = DatabaseConfigProvider.create();

// 结果
provider.getDbType()            // "h2" / "mysql" / "sqlserver" / "postgresql"
provider.getDriverClassName()   // 对应驱动
provider.getJdbcUrl()           // 连接串
provider.getUsername()          // 用户名
provider.getPassword()          // 密码
```

**优先级：**

| 来源 | System Property | 环境变量 | 默认值 |
|------|----------------|---------|--------|
| 数据库类型 | `db.type` | `DB_TYPE` | `h2` |
| JDBC URL | `db.url` | `DB_URL` | H2 内存数据库 |
| 用户名 | `db.user` | `DB_USER` | `sa` |
| 密码 | `db.password` | `DB_PASSWORD` | `sa` |

**支持的数据库类型：** `h2`, `mysql`, `sqlserver`, `sqlserver2012`, `postgresql`, `oracle`

**默认 H2 配置：**
```
jdbc:h2:mem:myfavs_test;DB_CLOSE_DELAY=-1;MODE=MYSQL
```

**使用示例（各平台）：**

环境变量方式完全避免 JDBC URL 中 `&` 字符的转义问题，在各平台表现一致，强烈推荐。

<details>
<summary><b>🐧 Linux / macOS (Bash)</b></summary>

```bash
export DB_TYPE=mysql
export DB_URL="jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8&useSSL=false"
export DB_USER=root
export DB_PASSWORD=root
mvn test -pl orm -P integration -Dtest=DatabaseTest
```
</details>

<details>
<summary><b>🪟 Windows (PowerShell)</b></summary>

```powershell
$env:DB_TYPE = "mysql"
$env:DB_URL  = "jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8&useSSL=false"
$env:DB_USER = "root"
$env:DB_PASSWORD = "root"
mvn test -pl orm -P integration -Dtest=DatabaseTest
```
</details>

<details>
<summary><b>🖥️ Windows (CMD)</b></summary>

```cmd
set DB_TYPE=mysql
set DB_URL=jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8^^^^useSSL=false
set DB_USER=root
set DB_PASSWORD=root
mvn test -pl orm -P integration -Dtest=DatabaseTest
```
</details>

### AbstractTest

```java
@Category(IntegrationTest.class)
public class AbstractTest {
    // 从 DatabaseConfigProvider 读取连接信息
    protected static final String DB_TYPE;       // = provider.getDbType()
    protected static final String JDBC_URL;      // = provider.getJdbcUrl()
    protected static final String JDBC_USERNAME; // = provider.getUsername()
    protected static final String JDBC_PASSWORD; // = provider.getPassword()

    @BeforeClass
    public static void beforeClass() {
        initDBTemplate();       // HikariDataSource → DBTemplate
        initDatabase();         // DBTemplate → Database
        createTables();         // 根据 DB_TYPE 自动选择 DDL 脚本
    }

    @AfterClass
    public static void afterClass() {
        database.close();
    }
}
```

**DDL 脚本选择逻辑：**

```java
private static String getSqlPath(String dbType) {
    switch (dbType) {
        case "mysql":         return "sql/mysql/myfavs_master.sql";
        case "sqlserver":
        case "sqlserver2012": return "sql/mssql/myfavs_master.sql";
        case "postgresql":    return "sql/postgresql/myfavs_master.sql";
        default:              return "sql/h2/myfavs_master.sql";
    }
}
```

**批处理分隔符差异：** SQL Server DDL 使用 `GO` 分隔批处理；其他数据库使用 `;` 分隔。

### 测试数据接口

通过接口混入（mixin）模式提供测试数据：

```java
public class DatabaseTest extends AbstractTest
    implements ISnowflakeTest, IIdentityTest, IUuidTest, ILogicDeleteTest, IAssignedTest {
    // 每个接口提供 initXxx() 方法和静态数据列表
}
```

### 测试实体

```
entity/
├── BaseEntity.java          # 抽象基类，含 created, name, disable, price, type, config 字段
├── SnowflakeExample.java    # @Table(strategy=SNOW_FLAKE)，主键 Long
├── IdentityExample.java     # @Table(strategy=IDENTITY)，主键自增
├── UuidExample.java         # @Table(strategy=UUID)，主键 UUID
├── LogicDeleteExample.java  # @Table(strategy=SNOW_FLAKE)，含 @LogicDelete
├── AssignedExample.java     # @Table(strategy=ASSIGNED)，手动主键
└── enums/
    └── TypeEnum.java        # FOOD, DRINK
```

### 组件单元测试覆盖

| 组件 | 测试文件 | 测试数 | 覆盖要点 |
|------|---------|--------|---------|
| OrmExecutor | `OrmExecutorTest.java` | 8 | SQL 字符串/Sql 对象/批量/超时/空集合 |
| OrmSelector | `OrmSelectorTest.java` | 10 | find/get/count/exists/findMap/null 边界 |
| OrmInserter | `OrmInserterTest.java` | 5 | null/空集合/Snowflake/Identity 策略 |
| OrmUpdater | `OrmUpdaterTest.java` | 7 | 单条/指定列/忽略Null/批量 CASE WHEN |
| OrmDeleter | `OrmDeleterTest.java` | 10 | 按实体/ID/IDs/条件/集合删除/逻辑删除/truncate |
| OrmPager | `OrmPagerTest.java` | 8 | PageLite/Page/IPageable/参数校验/Top |
| OrmSqlBuilder | `OrmSqlBuilderTest.java` | 12 | INSERT/SELECT/COUNT/UPDATE SQL 生成 |

### 建表 DDL

**位置：** `orm/src/test/resources/sql/{db}/myfavs_master.sql`

每张表统一结构（各数据库语法适配后）：

```
tb_assigned       — 手动主键测试
tb_identity       — 自增主键测试
tb_logic_delete   — 逻辑删除测试
tb_snowflake      — 雪花算法主键测试
tb_uuid           — UUID 主键测试
tb_tenant         — 多租户测试
tb_user           — 用户表
```

**各数据库特性差异处理：**

| 特性 | SQL Server | MySQL | PostgreSQL | H2 |
|------|-----------|-------|-----------|-----|
| 自增语法 | `IDENTITY(1,1)` | `AUTO_INCREMENT` | `BIGSERIAL` | `AUTO_INCREMENT` |
| 布尔类型 | `bit` | `tinyint(1)` | `boolean` | `boolean` |
| 时间类型 | `datetime` | `datetime` | `timestamp` | `datetime` |
| 分隔符 | `GO` | `;` | `;` | `;` |

---

## 21. DruidUtil — 阿里 Druid 桥梁

**文件：** `orm/src/main/java/work/myfavs/framework/orm/util/common/DruidUtil.java`

### 职责

将框架的 `dbType` 字符串（`"mysql"`, `"sqlserver"` 等）映射到 Alibaba Druid 的 `DbType` 枚举，提供 AST 节点构建方法。

### 核心方法

```java
// 类型映射
DbType toDbType(String dbType);  // "mysql" → DbType.mysql

// SQL 解析
SQLSelectStatement createSQLSelectStatement(String dbType, String sql);
// 用于分页 SQL 重写（MySqlOrm.limit(), SqlServerOrm.limit() 等）

// 节点构建
SQLExpr createColumn(String columnName);      // 创建列引用
SQLExpr createParam();                         // 创建参数占位符 ?
SQLUpdateSetItem createUpdateSetItem(String column);  // SET 项
SQLUpdateStatement createSQLUpdateStatement(String tableName);  // UPDATE 语句

// COUNT 查询
String count(String sql, String dbType);
// 委托 PagerUtils.count(sql, DbType)
```

### Druid 依赖

`druid` 是 optional 依赖。如果未引入，`DruidUtil.createSQLSelectStatement()` 等方法会因 `ClassNotFoundException` 失败。框架不强制 Druid，但分页和 SQL 构建器的高级功能依赖它。

---

## 22. 数据流与关键路径

### 事务内查询

```
@Transactional
Service.method()
  → Repository.find(sql)
    → try (Database db = dbTemplate.createDatabase())
      → SpringConnFactory.openConnection()
        → DataSourceUtils.getConnection(dataSource)  // 返回 Spring 事务连接
      → Orm orm = db.createOrm()
        → OrmFactory.createOrm(db) → MySqlOrm(db)
      → orm.find(viewClass, sql)
        → Query.createQuery(sql)
        → Query.addParameters(params)
        → Query.getPreparedStatement()  // 懒创建
        → Query.find(Class) → DBConvert.convert(rs, clazz)
    → auto-close: connFactory.closeConnection()
      → DataSourceUtils.releaseConnection(conn, ds)  // 不物理关闭
```

### 分页查询

```
orm.findPage(class, sql, enablePage, page, size)
  → 校验 maxPageSize
  → 生成 COUNT SQL（Druid PagerUtils.count）
  → 执行 COUNT
  → selectPage(sql, params, page, size)  // 方言实现
  → 执行分页 SQL
  → 返回 Page<T>
```

### 实体更新

```
orm.update(modelClass, entity)
  → entityMeta = Metadata.entityMeta(modelClass)
  → primaryKey = entityMeta.checkPrimaryKey()
  → updateAttributes = entityMeta.getUpdateAttributes()  // 排除主键和逻辑删除
  → SQL: UPDATE table SET col1=?, col2=? WHERE pk=? [AND logicDelete=0]
  → Query.addParameters(params).execute()
```

### 批量插入

```
orm.create(modelClass, entities)
  → entityMeta = Metadata.entityMeta(modelClass)
  → 遍历 entities:
    → generatePrimaryKey(entityMeta, entity)  // 预生成主键
    → 构建 INSERT SQL
  → 如果是 MySQL 且非 IDENTITY:
    → 构建单条多 VALUES 语句: INSERT INTO table VALUES (?,?), (?,?), (?,?)
    → 一次 execute()
  → 如果是 SQL Server IDENTITY:
    → 逐条 INSERT, getGeneratedKeys() 回读主键
  → 其他:
    → 批量 addBatch() + executeBatch()
```
