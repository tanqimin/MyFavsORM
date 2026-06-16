[![GitHub release](https://img.shields.io/github/stars/tanqimin/myfavs.framework?style=flat-square)](https://github.com/tanqimin/myfavs.framework)

# MyFavs ORM

轻量级 Java ORM 框架，为擅长手写 SQL 的开发者设计。如果您厌倦了 MyBatis 复杂的 XML 语法，MyFavs ORM 提供了更简洁、更直观的数据库操作方式。

## 核心特性

- **双模式操作** — `Query` 类追求极致性能（原生 JDBC 封装），`Orm` 类追求研发效率（实体映射 + 自动 CRUD）
- **注解驱动映射** — `@Table` / `@Column` / `@PrimaryKey` 标注实体，默认驼峰转下划线命名
- **多种主键策略** — 雪花算法（Snowflake）、UUID、数据库自增（IDENTITY）、手动赋值（ASSIGNED）
- **SQL 构建器** — 链式 API 构建 SQL，配合 `Cond` 条件构建器，安全防注入
- **分页支持** — `Page<T>`（含总行数）、`PageLite<T>`（轻量级，含 hasNext），多数据库方言自动适配
- **多数据库支持** — MySQL、SQL Server（2005+/2012+）、PostgreSQL、Oracle、H2
- **事务管理** — `Database.tx()` 函数式事务包装器，支持保存点（Savepoint）
- **Spring Boot 集成** — 通过 `SpringConnFactory` 无缝对接 Spring 声明式事务（`@Transactional`）
- **PropertyHandler 类型系统** — 可扩展的类型处理器，支持自定义 Java 类型与 JDBC 类型的双向映射
- **轻量级** — 核心模块仅依赖 `slf4j-api` 和 `druid`（均为 optional），不绑定特定连接池

## 技术栈

| 类别 | 技术 | 说明 |
|---|---|---|
| 运行环境 | Java 11+ | 编译目标 Java 11 |
| 日志 | slf4j-api 2.0.18 | optional，由下游提供实现 |
| SQL AST | Alibaba Druid 1.2.28 | optional，用于分页 SQL 改写和格式化 |
| 连接池 | HikariCP 7.0.2（test）/ Druid（可选） | 不强制绑定 |
| Spring | spring-jdbc 5.3.39（optional） | 仅 `SpringConnFactory` 需要 |
| 测试 | JUnit 4.13.2 / Mockito 5.23.0 | @Category 分离集成测试 |
| 集成测试数据库 | H2（默认）/ MySQL / SQL Server / PostgreSQL | System Property 或环境变量切换 |

## 快速入门

### 1. 添加 Maven 依赖

```xml
<!-- 核心库 -->
<dependency>
    <groupId>work.myfavs.framework</groupId>
    <artifactId>orm</artifactId>
    <version>1.0.0-260610-1</version>
</dependency>

<!-- Spring Boot 集成（可选） -->
<dependency>
    <groupId>work.myfavs.framework</groupId>
    <artifactId>orm-spring-boot2-starter</artifactId>
    <version>1.0.0-260610-1</version>
</dependency>
```

### 2. 定义实体类

```java
import work.myfavs.framework.orm.meta.annotation.*;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(value = "tb_product", strategy = GenerationType.SNOW_FLAKE)
public class Product implements Serializable {

    @PrimaryKey
    @Column
    private Long          id;

    @Column
    private LocalDateTime created;

    @Column
    private String        name;

    @Column
    private boolean       disable;

    @Column
    private BigDecimal    price = BigDecimal.ZERO;

    // getter / setter 省略...
}
```

**注解说明：**

| 注解 | 作用域 | 说明 |
|---|---|---|
| `@Table(value, strategy)` | 类 | 标记实体。`value` 为空时表名 = 类名转下划线小写；`strategy` 为主键策略，默认 `SNOW_FLAKE` |
| `@Column(value, readonly)` | 字段 | 标记数据库列。`value` 为空时列名 = 字段名转下划线小写；`readonly=true` 时该列不参与 INSERT/UPDATE |
| `@PrimaryKey` | 字段 | 标记主键字段，必须与 `@Column` 配合使用 |
| `@LogicDelete` | 字段 | 标记逻辑删除字段，删除操作时将该字段值置为主键值而非物理删除 |
| `@Criterion(value, operator, order, group)` | 字段 | 可重复注解，配合 `Cond.criteria()` 自动生成查询条件 |

### 3. 配置 DBTemplate

```java
HikariConfig config = new HikariConfig();
config.setDriverClassName("com.mysql.cj.jdbc.Driver");
config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mydb?...");
config.setUsername("root");
config.setPassword("root");
config.setAutoCommit(false);

DataSource dataSource = new HikariDataSource(config);

DBTemplate dbTemplate = new DBTemplate.Builder()
    .dataSource(dataSource)
    .config(c -> c
        .setDbType("mysql")    // 数据库类型，支持 mysql/sqlserver/sqlserver2012/postgresql/oracle/h2
        .setShowSql(true)      // 打印 SQL 日志
        .setShowResult(true)   // 打印查询结果日志
    )
    .build();
```

**DBConfig 常用配置项：**

| 参数 | 默认值 | 说明 |
|---|---|---|
| `dbType` | `"mysql"` | 数据库类型 |
| `showSql` | `false` | 是否打印 SQL 及参数 |
| `showResult` | `false` | 是否打印查询结果 |
| `batchSize` | `200` | 批量操作每批处理数量 |
| `fetchSize` | `1000` | ResultSet 每次抓取行数 |
| `queryTimeout` | `60` | 查询超时秒数 |
| `maxPageSize` | `-1`（不限制）| 分页每页最大记录数 |
| `workerId` | `1` | 雪花算法 Worker ID（0-31） |
| `dataCenterId` | `1` | 雪花算法数据中心 ID（0-31） |

---

## 使用指南

### Query 类 — 原生 SQL 操作

适合追求极致性能、需要完全控制 SQL 的场景。

#### 查询（find）

```java
try (Database database = dbTemplate.createDatabase();
     Query query = database.createQuery("SELECT * FROM tb_product WHERE name = ?")) {

    query.addParameter("可口可乐");
    List<Record> records = query.find(Record.class);
}
```

#### 执行（execute）

```java
try (Database database = dbTemplate.createDatabase();
     Query query = database.createQuery("INSERT INTO tb_product(code, name) VALUES (?, ?)")) {

    query.addParameter("KELE").addParameter("可口可乐").execute();
    query.addParameter("ICETEA").addParameter("冰红茶").execute();
}
```

#### 批量执行（executeBatch）

```java
try (Database database = dbTemplate.createDatabase();
     Query query = database.createQuery("INSERT INTO tb_product(code, name) VALUES (?, ?)")) {

    query.addParameter("KELE").addParameter("可口可乐").addBatch();
    query.addParameter("ICETEA").addParameter("冰红茶").addBatch();
    int[] results = query.executeBatch();
}
```

### Orm 类 — 实体 CRUD 操作

适合追求研发效率、需要对象-关系映射的场景。

#### Sql 构建器

```java
// 方式一：直接拼接 SQL + 参数
Sql sql = new Sql("SELECT * FROM tb_product WHERE id = ?", 1L);

// 方式二：链式构建
Sql sql = new Sql("SELECT * FROM tb_product")
    .WHERE(Cond.eq("id", 1L));

// 方式三：全链式
Sql sql = new Sql()
    .SELECT("*")
    .FROM("tb_product")
    .WHERE(Cond.eq("id", 1L));
```

#### Cond 条件构建器

`Cond` 提供丰富的条件方法，支持链式组合：

```java
// 等值比较
Cond.eq("name", "手机");
Cond.eq("name", "手机", false);  // value 为 null 时忽略该条件（默认 true 忽略）

// 模糊查询
Cond.like("name", "%手机%");

// 区间查询 + 列表查询
Cond.between("price", 100, 500);
Cond.in("id", Arrays.asList(1, 2, 3));

// 链式组合 AND / OR
Cond.eq("status", 1).and(Cond.gt("price", 100));
Cond.like("name", "%手机%").or(Cond.eq("type", "电子"));

// 从 @Criterion 注解自动生成条件
Cond.criteria(product, BaseEntity.Update.class);  // 按 group 分组生成
```

#### 查询单行

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    // 根据主键
    Product product = orm.getById(Product.class, 1L);

    // 根据 SQL
    Product product = orm.get(Product.class, new Sql("SELECT * FROM tb_product WHERE id = ?", 1L));

    // 根据字段值
    Product product = orm.getByField(Product.class, "name", "可口可乐");
}
```

#### 查询多行

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    // 全量查询
    List<Product> products = orm.find(Product.class, new Sql("SELECT * FROM tb_product"));

    // 条件查询
    List<Product> products = orm.find(Product.class,
        new Sql("SELECT * FROM tb_product").WHERE(Cond.like("name", "%手机%")));

    // 简单条件查询
    List<Product> products = orm.findByCond(Product.class, Cond.like("name", "%手机%"));

    // 按字段查询
    List<Product> products = orm.findByField(Product.class, "name", "可口可乐");

    // 按主键集合查询
    List<Product> products = orm.findByIds(Product.class, Arrays.asList(1L, 2L, 3L));

    // 查询前 N 条
    List<Product> products = orm.findTop(Product.class, 10,
        new Sql("SELECT * FROM tb_product").ORDER_BY("id DESC"));

    // 查询单列值（返回 ID 列表）
    List<Long> ids = orm.find(Long.class, new Sql("SELECT id FROM tb_product"));

    // 查询返回 Map<PK, Entity>
    Map<Long, Product> map = orm.findMap(Product.class, "id",
        new Sql("SELECT * FROM tb_product"));
}
```

#### 插入

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    Product product = new Product();
    product.setName("可口可乐");
    product.setPrice(new BigDecimal("3.50"));
    orm.create(product);           // 单条插入，主键自动回填

    List<Product> list = Arrays.asList(p1, p2, p3);
    orm.create(list);              // 批量插入
}
```

#### 修改

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    Product product = orm.getById(Product.class, 1L);
    product.setPrice(new BigDecimal("4.00"));
    orm.update(product);           // 全字段更新

    // 只更新指定字段
    orm.update(product, new String[]{"price"});

    List<Product> list = Arrays.asList(p1, p2, p3);
    orm.update(list);              // 批量更新
}
```

#### 删除

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    orm.delete(product);                 // 按实体删除
    orm.deleteById(Product.class, 1L);   // 按主键删除
    orm.deleteByIds(Product.class, Arrays.asList(1L, 2L, 3L));  // 按主键集合删除
}
```

> **逻辑删除**：如果实体用 `@LogicDelete` 标注了逻辑删除字段，执行 `delete()` 时会将主键值写入该字段而非物理删除。

#### 统计与判断

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    long count = orm.count(new Sql("SELECT * FROM tb_product").WHERE(Cond.gt("price", 10)));
    boolean exists = orm.exists(new Sql("SELECT * FROM tb_product WHERE id = ?", 1L));
}
```

### 分页查询

```java
Sql sql = new Sql("SELECT * FROM tb_product").WHERE(Cond.like("name", "%手机%"));

try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    // 完整分页（含总页数、总行数）
    Page<Product> page = orm.findPage(Product.class, sql, true, 1, 20);
    // page.getData()        → 当前页数据
    // page.getTotalPages()  → 总页数
    // page.getTotalRecords() → 总行数

    // 轻量分页（仅含 hasNext，无 COUNT 查询）
    PageLite<Product> pageLite = orm.findPageLite(Product.class, sql, true, 1, 20);
    // pageLite.isHasNext()  → 是否有下一页
}
```

**配合 `IPageable` 接口**（请求对象直接控制分页参数）：

```java
public class PageRequest implements IPageable {
    private int currentPage = 1;
    private int pageSize = 20;

    @Override public boolean getEnablePage() { return true; }
    @Override public int getCurrentPage() { return currentPage; }
    @Override public int getPageSize() { return pageSize; }
}

// 使用
Page<Product> page = orm.findPage(Product.class, sql, pageRequest);
```

分页结果字段名可通过 `DBConfig` 自定义（如 `pageDataField`、`pageCurrentField` 等）。

### 事务管理

```java
try (Database database = dbTemplate.createDatabase()) {
    database.tx(orm -> {
        orm.update(product);
        orm.delete(oldProduct);
    });
    // 成功自动 commit，异常自动 rollback
}
```

**带保存点的事务：**

```java
try (Database database = dbTemplate.createDatabase()) {
    database.tx(orm -> {
        orm.create(product1);
        Savepoint sp = database.setSavepoint("sp1");
        try {
            orm.create(product2);
        } catch (Exception e) {
            database.rollback(sp);  // 回滚到保存点，product1 保留
        }
        orm.create(product3);
    });
}
```

### 同构表（分表）查询

通过 `TableAlias` 在运行时动态切换表名，适用于按租户或日期分表的场景：

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    // 方式一：手动设置 / 清除
    TableAlias.set("order_2024");
    List<Order> orders = orm.find(Order.class, new Sql("SELECT * FROM order_2024"));
    TableAlias.clear();  // 用完后必须清除！

    // 方式二：Lambda 自动清除（推荐）
    List<Order> orders = TableAlias.function("order_2024",
        alias -> orm.find(Order.class, new Sql("SELECT * FROM order_2024")));
}
```

---

## Spring Boot 整合

### 配置类

```java
@Configuration
public class MyFavsConfig {

    @Bean
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    public DBTemplate dbTemplate(DataSource dataSource) {
        return new DBTemplate.Builder()
            .dataSource(dataSource)
            .connectionFactory(SpringConnFactory.class)  // 关键：启用 Spring 事务集成
            .config(c -> c
                .setDbType("mysql")
                .setBatchSize(200)
                .setFetchSize(100)
                .setWorkerId(1L)
                .setDataCenterId(1L))
            .mapping(m -> m
                .register(String.class, new StringPropertyHandler())
                .register(long.class, new LongPropertyHandler(true))
                .register(Long.class, new LongPropertyHandler())
                .register(BigDecimal.class, new BigDecimalPropertyHandler())
                .register(Date.class, new DatePropertyHandler()))
            .build();
    }
}
```

> **关键配置**：`connectionFactory(SpringConnFactory.class)` 使 ORM 的数据库连接由 Spring 的 `DataSourceUtils` 管理，从而与 `@Transactional` 共享同一连接和事务上下文。

### Repository

```java
@org.springframework.stereotype.Repository
public class ProductQuery extends work.myfavs.framework.orm.repository.Query {
    @Autowired
    public ProductQuery(@Qualifier("dbTemplate") DBTemplate dbTemplate) {
        super(dbTemplate);
    }
}

@org.springframework.stereotype.Repository
public class ProductRepository extends work.myfavs.framework.orm.repository.Repository<Product> {
    @Autowired
    public ProductRepository(@Qualifier("dbTemplate") DBTemplate dbTemplate) {
        super(dbTemplate);
    }
}
```

继承链：`SimpleRepository` → `BaseRepository`（只读快捷方法） → `Query`（分页 + Record） → `Repository<T>`（实体 CRUD）。

### 配合 Spring 事务

```java
@Service
public class ProductService extends BaseService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Product save(Product product) {
        productRepository.create(product);
        return product;
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, Product entity) {
        Product product = productRepository.getById(id);
        product.setName(entity.getName());
        product.setPrice(entity.getPrice());
        productRepository.update(product);
    }

    public Page<Product> findByPage() {
        return productRepository.findPage(Product.class,
            new Sql("SELECT * FROM tb_product"), true, 1, 10);
    }
}
```

> **多租户示例**：`demos/spring-boot2-demo` 展示了基于 `AbstractRoutingDataSource` + AOP 的运行时多数据源切换方案，可作为参考实现。

---

## 高级特性

### PropertyHandler 类型处理器

`PropertyHandler<T>` 负责 Java 类型与 JDBC 类型的双向转换。框架默认注册 12 种类型处理器，其余类型通过 `ObjectPropertyHandler` 兜底：

```
PropertyHandler<T>
  ├── StringPropertyHandler / NVarcharPropertyHandler
  ├── NumberPropertyHandler (数值类型抽象基类)
  │     ├── IntegerPropertyHandler (int/Integer, 基础类型和包装类分开)
  │     ├── LongPropertyHandler    (long/Long)
  │     ├── DoublePropertyHandler  (double/Double)
  │     ├── FloatPropertyHandler   (float/Float)
  │     ├── ShortPropertyHandler   (short/Short)
  │     ├── BytePropertyHandler    (byte/Byte)
  │     ├── BooleanPropertyHandler (boolean/Boolean)
  │     └── BigDecimalPropertyHandler
  ├── DatePropertyHandler / LocalDateTimePropertyHandler / OffsetDateTimePropertyHandler
  ├── UUIDPropertyHandler / EnumPropertyHandler
  ├── BlobPropertyHandler / ClobPropertyHandler / ByteArrayPropertyHandler
  └── ObjectPropertyHandler (兜底，rs.getObject / ps.setObject)
```

**自定义类型处理器示例：**

```java
public class UUIDPropertyHandler extends PropertyHandler<UUID> {

    @Override
    public UUID convert(ResultSet rs, int columnIndex, Class<UUID> clazz) throws SQLException {
        return ConvertUtil.toUUID(rs.getObject(columnIndex));
    }

    @Override
    public void addParameter(PreparedStatement ps, int paramIndex, UUID param) throws SQLException {
        ps.setString(paramIndex, param.toString());
    }

    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }
}

// 注册
new DBTemplate.Builder()
    .mapping(m -> m.register(UUID.class, new UUIDPropertyHandler()))
    .build();
```

> **注册策略**：框架始终先注册 12 种内置默认处理器。用户通过 `.mapping()` 注册的自定义处理器会按类型覆盖同名的默认处理器，未覆盖的类型仍使用默认处理器（**"默认 + 自定义覆盖"** 模式）。未注册的类型走兜底逻辑：枚举类型返回 `EnumPropertyHandler`，其他类型返回 `ObjectPropertyHandler`。
>
> **注意**：基础类型（`int.class`）和包装类（`Integer.class`）必须分开注册，因为 `PropertyHandlerFactory` 以 Class 对象精确匹配 key。

### 主键策略

| 策略 | 生成方式 | 适用场景 |
|---|---|---|
| `SNOW_FLAKE`（默认） | Twitter Snowflake 算法，生成 `long` 型 ID | 分布式系统推荐 |
| `UUID` | `UUID.randomUUID().toString()` | 需要全局唯一字符串 ID |
| `IDENTITY` | 数据库自增（`getGeneratedKeys()` 回读） | 单库自增场景 |
| `ASSIGNED` | 手动赋值，主键为 null 时抛异常 | 业务主键（如订单号） |

### 异常体系

```
RuntimeException
  └── DBException                       ← 框架异常总基类
       ├── ConnectionException          ← 连接获取、事务提交/回滚、保存点失败
       ├── DataRetrievalException       ← SQL 查询、更新、批量操作、参数绑定失败
       ├── InvalidDataAccessException   ← 配置错误、类型转换、反射失败
       └── PaginationException          ← 分页参数越界
```

使用 `catch (DBException e)` 可统一捕获所有 ORM 异常，也可按子类细化处理。

---

## 构建与运行

### 环境要求

- JDK 11+
- Maven 3.6+

### 构建命令

```bash
# 全量构建（跳过测试）
mvn clean install -DskipTests

# 仅编译核心模块
mvn compile -pl orm

# 运行纯单元测试（不依赖外部数据库，默认使用 H2 内存数据库）
mvn test -pl orm

# 运行单个测试类
mvn test -pl orm -Dtest=CondTest

# 运行单个测试方法
mvn test -pl orm -Dtest=CondTest#eq

# 查看可用依赖更新
mvn versions:display-dependency-updates

# 查看可用插件更新
mvn versions:display-plugin-updates

# 变更版本号（格式：1.0.0-YYMMDD-N）
mvn versions:set -DnewVersion="1.0.0-260710-1"
```

### 测试说明

**纯单元测试**（默认 `mvn test`）：
- 使用 Mockito 模拟数据库，**无需真实数据库连接**
- `@Category(IntegrationTest.class)` 标记的类被自动排除
- 包含 **333+** 个测试用例，覆盖 `OrmExecutor`、`OrmSelector`、`OrmInserter`、`OrmUpdater`、`OrmDeleter`、`OrmPager`、`Snowflake`、`ReflectUtil`、`Order`、`TableAlias` 等核心组件

**集成测试**（需指定数据库）：
- 通过 `DatabaseConfigProvider` 按优先级读取：系统属性 → 环境变量 → 默认值
- 默认使用 **H2 内存数据库**（无需安装），也可指定其他数据库
- 集成测试类有 `@Category(IntegrationTest.class)`，需通过 `-P integration` 激活 profile 以取消排除

推荐使用环境变量方式传递数据库配置，避免跨平台转义问题（如 `&` 字符、行续符差异）：

<details>
<summary><b>🐧 Linux / macOS (Bash)</b></summary>

```bash
# H2 内存数据库（默认，无需安装）
mvn test -pl orm -P integration -Dtest=DatabaseTest

# MySQL
export DB_TYPE=mysql
export DB_URL="jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8"
export DB_USER=root
export DB_PASSWORD=root
mvn test -pl orm -P integration -Dtest=DatabaseTest

# SQL Server
export DB_TYPE=sqlserver
export DB_URL="jdbc:sqlserver://192.168.8.246:1433;DatabaseName=myfavs_master;encrypt=false"
export DB_USER=sa
export DB_PASSWORD=sa
mvn test -pl orm -P integration -Dtest=DatabaseTest

# PostgreSQL
export DB_TYPE=postgresql
export DB_URL="jdbc:postgresql://localhost:5432/myfavs_master"
export DB_USER=postgres
export DB_PASSWORD=postgres
mvn test -pl orm -P integration -Dtest=DatabaseTest
```
</details>

<details>
<summary><b>🪟 Windows (PowerShell)</b></summary>

```powershell
# H2 内存数据库（默认，无需安装）
mvn test -pl orm -P integration -Dtest=DatabaseTest

# MySQL
$env:DB_TYPE = "mysql"
$env:DB_URL  = "jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=GMT%2B8"
$env:DB_USER = "root"
$env:DB_PASSWORD = "root"
mvn test -pl orm -P integration -Dtest=DatabaseTest

# SQL Server
$env:DB_TYPE = "sqlserver"
$env:DB_URL  = "jdbc:sqlserver://192.168.8.246:1433;DatabaseName=myfavs_master;encrypt=false"
$env:DB_USER = "sa"
$env:DB_PASSWORD = "sa"
mvn test -pl orm -P integration -Dtest=DatabaseTest

# PostgreSQL
$env:DB_TYPE = "postgresql"
$env:DB_URL  = "jdbc:postgresql://localhost:5432/myfavs_master"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "postgres"
mvn test -pl orm -P integration -Dtest=DatabaseTest
```
</details>

<details>
<summary><b>🖥️ Windows (CMD)</b></summary>

```cmd
REM H2 内存数据库（默认，无需安装）
mvn test -pl orm -P integration -Dtest=DatabaseTest

REM MySQL
set DB_TYPE=mysql
set DB_URL=jdbc:mysql://localhost:3306/myfavs_master?characterEncoding=utf-8^^^^useSSL=false^^^^allowPublicKeyRetrieval=true^^^^serverTimezone=GMT%%2B8
set DB_USER=root
set DB_PASSWORD=root
mvn test -pl orm -P integration -Dtest=DatabaseTest

REM SQL Server
set DB_TYPE=sqlserver
set DB_URL=jdbc:sqlserver://192.168.8.246:1433;DatabaseName=myfavs_master;encrypt=false
set DB_USER=sa
set DB_PASSWORD=sa
mvn test -pl orm -P integration -Dtest=DatabaseTest

REM PostgreSQL
set DB_TYPE=postgresql
set DB_URL=jdbc:postgresql://localhost:5432/myfavs_master
set DB_USER=postgres
set DB_PASSWORD=postgres
mvn test -pl orm -P integration -Dtest=DatabaseTest
```

> CMD 中 `&` 是命令分隔符，需要用 `^^^^` 转义；`%` 需要用 `%%` 转义。
> 如需单行执行，用 `&&` 连接：`set DB_TYPE=mysql && mvn test ...`
</details>

> **提示**：环境变量方式完全避免 JDBC URL 中 `&` 字符的转义问题，在各平台表现一致，强烈推荐。`DatabaseConfigProvider` 按优先级读取：系统属性 → 环境变量 → H2 默认值。

- 测试基类 `AbstractTest` 在 `@BeforeClass` 中自动根据数据库类型选择对应 DDL 脚本
- 建表脚本按数据库类型分离：

```
orm/src/test/resources/sql/
├── mssql/      myfavs_master.sql     ← SQL Server
├── mysql/      myfavs_master.sql     ← MySQL
├── postgresql/ myfavs_master.sql     ← PostgreSQL
└── h2/         myfavs_master.sql     ← H2
```

---

## Maven 坐标

```xml
<!-- 核心库（仅 optional 依赖 slf4j-api + druid） -->
<dependency>
    <groupId>work.myfavs.framework</groupId>
    <artifactId>orm</artifactId>
    <version>1.0.0-260610-1</version>
</dependency>

<!-- Spring Boot Starter（依赖 orm + optional spring-jdbc） -->
<dependency>
    <groupId>work.myfavs.framework</groupId>
    <artifactId>orm-spring-boot2-starter</artifactId>
    <version>1.0.0-260610-1</version>
</dependency>
```

发布至 [Maven Central](https://central.sonatype.com/)（Sonatype OSSRH），需要时添加 Sonatype 仓库：

```xml
<repositories>
    <repository>
        <id>sonatype-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>
```

---

## 开发者参考

如需深入理解框架源码、调试或二次开发，请参阅 [`CODE_WIKI.md`](CODE_WIKI.md)，涵盖：

- **包结构总览**与核心类职责
- **22 个章节**：DBTemplate、Database、ConnFactory、Query、Orm/AbstractOrm、SQL 构建、元数据、PropertyHandler、分页、Spring Boot 集成等
- **数据流与关键路径**：事务内查询、分页查询、实体更新、批量插入的完整调用链路
- **6 大组件职责表**与 SQL Server（2100 参数限制）特殊处理

---

## 许可

[Apache License 2.0](LICENSE)
