[![GitHub release](https://img.shields.io/github/stars/tanqimin/myfavs.framework?style=flat-square)](https://github.com/tanqimin/myfavs.framework)
# MyFavs ORM
##### A light-weight ORM Framework
如果您厌倦了MyBatis复杂的 XML 语法，并且擅长 SQL 的编写，可以试试 MyFavs ORM。
## Quick Start

首先，我们需要创建数据源，这里使用`HikariDataSource`作为示例：

```java
HikariConfig configuration = new HikariConfig();
configuration.setDriverClassName("com.mysql.jdbc.Driver");
configuration.setJdbcUrl(url);
configuration.setUsername(user);
configuration.setPassword(password);
configuration.setAutoCommit(false);
DataSource dataSource = new HikariDataSource(configuration);
```



然后，在程序启动的时候，创建`DBTemplate`：

```java
DBTemplate dbTemplate = new DBTemplate.Builder()
        .dataSource(dataSource)
        .config(config -> {
            config
                .setShowSql(true)
                .setShowResult(true);
        }).build();
```

然后就可以马上使用：

```java
try (Database database = dbTemplate.createDatabase();
     Query query = database.createQuery("SELECT * FROM tb_product")) {
    List<Record> records = query.find(Record.class);
}
```

## 使用入门

### Query类

> 在一些系统中追求极致性能，可以直接使用Query类进行数据库操作

#### find方法

```java
try (Database database = dbTemplate.createDatabase();
     Query query = database.createQuery("SELECT * FROM tb_product WHERE name = ?")) {
    List<Record> records = query.addParameter(1, "可口可乐").find(Record.class);
}
```

#### execute方法

```java
try (Database database = dbTemplate.createDatabase();
     Query query = database.createQuery("INSERT INTO tb_product(code, name) VALUE (?, ?)")) {
    query.addParameter( "KELE").addParameter("可口可乐").execute();
    query.addParameter( "ICETEA").addParameter("冰红茶").execute();
}
```

#### executeBatch方法

```java
try (Database database = dbTemplate.createDatabase();
     Query query = database.createQuery("INSERT INTO tb_product(code, name) VALUE (?, ?)")) {
    query.addParameter( "KELE").addParameter("可口可乐").addBatch();
    query.addParameter( "ICETEA").addParameter("冰红茶").addBatch();
    query.executeBatch();
}
```



### Orm类

> 如果注重研发效率，可使用Orm类对实体进行操作

#### 查询

以下示例，创建一个实体类

```java
@Data
@Table(value = "tb_product", strategy = GenerationType.SNOW_FLAKE)
public class Product implements Serializable {
    @PrimaryKey
    @Column
    private Long          id;
    @Column
    private LocalDateTime created;
    @Column
    @NVarchar
    private String        name;
    @Column
    private boolean       disable;
    @Column
    private BigDecimal    price = BigDecimal.ZERO;
}
```

此处用到`@Table`、`@Column`、和`@PrimaryKey`注解：

+ `@Table`，定义实体的数据表，其中参数value为数据表名称(如果不设置value，则会把实体名称转成下划线分隔，小写的形式（如实体名称为 *ProductPrice*，对应的数据表名称为 *product_price*），strategy为主键策略；
    * GenerationType.UUID，UUID，如果主键值为null，会自动生成；
    * GenerationType.SNOW_FLAKE，雪花值，，如果主键值为null，会自动生成；
    * GenerationType.IDENTITY，数据库自增，值由数据库生成；
    * GenerationType.ASSIGNED，自然主键，值由用户自定义；
+ `@Column`，定义实体类关联的数据表字段，参数value为数据字段名称(如果不设置value，则会把实体属性名称转成下划线分隔，小写的形式（如实体名称为 *productCode*，对应的数据字段名称为 *product_code*）；参数readOnly默认值为false，当设置为true时，插入和更新操作不会包含该字段；
+ `@PrimaryKey`，定义主键的属性，必须和`@Column`配合使用；

##### Sql构建器

###### 创建Sql构建器

```java
Sql sql = new Sql();
//或
Sql sql = Sql.New();
```

###### 创建查询语句

```java
Sql sql = new Sql("SELECT * FROM tb_product WHERE id = ?", 1L);
//或
Sql sql = new Sql("SELECT * FROM tb_product").where(Cond.eq("id", 1L));
//或
Sql sql = new Sql().select("*").from("tb_product").where(Cond.eq("id", 1L));
```

###### 条件构建类Cond

对于系统中，大家会发现where、and、or等方法中的参数，都是条件构建器`Cond`：

```java
//where id = 1
sql.where(Cond.eq("id", 1));

//where 1 = 1 and id = 1;
sql.where().and(Cond.eq("id", 1));

//where 1 = 1 id in (1, 2, 3)
ArrayList<Integer> params = new ArrayList<>();
Collections.addAll(list, 1,2,3);
sql.where().and(Cond.in("id", params));

//如果params为null，或空集合，默认忽略条件;
params.clear();
sql.where().and(Cond.in("id", params));			//where 1 = 1
sql.where().and(Cond.in("id", params, false));	 //where 1 = 1 and 1 > 2
```



##### 查询一行记录

```java
Sql sql = new Sql("SELECT * FROM tb_product WHERE id = ?", 1);
try (Database database = dbTemplate.createDatabase()) {
    Orm     orm     = database.createOrm();
    Product product = orm.get(Product.class, sql);
}
```

##### 根据主键查询

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm     orm     = database.createOrm();
    Product product = orm.getById(Product.class, 1);
}
```

##### 查询多行记录

```java
Sql sql = new Sql("SELECT * FROM tb_product").where(Cond.like("name", "%手机%"));
try (Database database = dbTemplate.createDatabase()) {
    Orm           orm      = database.createOrm();
    List<Product> products = orm.find(Product.class, sql);
}
```

##### 简单条件查询

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm           orm      = database.createOrm();
    List<Product> products = orm.findByCond(Product.class, Cond.like("name", "%手机%"));
}
```

##### 查询返回ID集合

```java
Sql sql = new Sql("SELECT id FROM tb_product").where(Cond.like("name", "%手机%"));
try (Database database = dbTemplate.createDatabase()) {
    Orm        orm      = database.createOrm();
    List<Long> products = orm.find(Long.class, sql);
}
```

##### 根据某个字段查询

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm           orm      = database.createOrm();
    List<Product> products = orm.findByField(Product.class, "name", "手机");
}
```

##### 查询前N条记录

```java
Sql sql = new Sql("SELECT * FROM tb_product").where(Cond.like("name", "%手机%"));
try (Database database = dbTemplate.createDatabase()) {
    Orm           orm      = database.createOrm();
    List<Product> products = orm.findTop(Product.class, 10, sql);
}
```

##### 根据主键集合查询

```java
ArrayList<Integer> params = new ArrayList<>();
Collections.addAll(list, 1,2,3);
try (Database database = dbTemplate.createDatabase()) {
    Orm           orm      = database.createOrm();
    List<Product> products = orm.findByIds(Product.class, params);
}
```

##### 查询返回Map

很多时候，我们希望查询返回Map<TPk, TEntity>的结构，可以这样写：

```java
Sql sql = new Sql("SELECT * FROM tb_product").where(Cond.like("name", "%手机%"));
try (Database database = dbTemplate.createDatabase()) {
    Orm           	   orm      = database.createOrm();
    Map<Long, Product> products = orm.findMap(Product.class, "id", sql);
}
```

##### 查询记录数

```java
Sql sql = new Sql("SELECT * FROM tb_product").where(Cond.like("name", "%手机%"));
try (Database database = dbTemplate.createDatabase()) {
    Orm  orm   = database.createOrm();
    long count = orm.count(sql);
}
```

##### 检查是否存在记录

```java
Sql sql = new Sql("SELECT * FROM tb_product").where(Cond.like("name", "%手机%"));
try (Database database = dbTemplate.createDatabase()) {
    Orm     orm    = database.createOrm();
    boolean exists = orm.exists(sql);
}
```

##### 分页查询

```java
Sql sql = new Sql("SELECT * FROM tb_product").where(Cond.like("name", "%手机%"));
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();
    //第3个参数为是否启用分页, 第4个参数为当前页码, 第5个参数为每页记录数
    Page<Product> page = orm.findPage(Product.class, sql, true, 1, 20);
}
```

建议在分页请求中实现`IPageable`接口：

```java
public PageRequest implements IPageable {}
```

分页查询可写为：

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm           orm  = database.createOrm();
    Page<Product> page = orm.findPage(Product.class, sql, pageRequest);
}
```

简单分页查询，结果不包含总行数，在某些考虑性能的情况下，可以使用；

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm               orm  = database.createOrm();
    PageLite<Product> page = orm.findPageLite(Product.class, sql, pageRequest);
}
```

#### 插入

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm  = database.createOrm();
    
    Product p1 = ..;
    orm.create(p1);
    
    List<Product> products = ...;
    orm.create(products);
}
```

#### 修改

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm  = database.createOrm();
    
    Product p1 = ..;
    orm.update(p1);
    
    List<Product> products = ...;
    orm.update(products);
    
    //只更新name字段
    orm.update(products, new String[]{"name"});
}
```

#### 删除

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm  = database.createOrm();
    
    Product p1 = ..;
    orm.delete(p1);
    
    List<Product> products = ...;
    orm.delete(products);
    
    //根据id删除
    orm.deleteById(1);
    
    ArrayList<Integer> params = new ArrayList<>();
    Collections.addAll(list, 1,2,3);
    orm.deleteByIds(params);
}
```

#### 事务

```java
try (Database database = dbTemplate.createDatabase()) {
    database.tx(orm -> {
        orm.update(p1);
        orm.delete(p2);
    });
}
```

## 高级使用

### 同构表（分表）查询

在一些业务场景需要使用另外一个同构表进行操作，可以使用TableAlias类进行操作，假设我们需要根据用户区域进行分表，原始数据表为order，分表为order_1

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    TableAlias.set("order_1");
    Order order = orm.getById(Order.class, id);

    //此时查询的语句为：select * from order_1 where id = ?
    TableAlias.clear();    //用完后记得调用clear()方法恢复原表名哦

    Order order = orm.getById(Order.class, id);
    //此时查询的语句为：select * from order where id = ?
}
```

也可以使用以下方式查询：

```java
try (Database database = dbTemplate.createDatabase()) {
    Orm orm = database.createOrm();

    Order order = TableAlias.function(
        "order_1",	//分表名称
        s -> orm.getById(Order.class, id));
    //此时查询的语句为：select * from order_1 where id = ?

    Order order = orm.getById(Order.class, id);
    //此时查询的语句为：select * from order where id = ?
}
```



## 整合SpringBoot

### 配置类

如果需要使用实体映射，需要在创建Orm对象的时候，对其进行配置
```java
@Configuration
public class MyFavsConfig {
    @Bean
    public DataSource dataSource(){
        //创建数据源
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    public DBTemplate dbTemplate(){
        return new DBTemplate.Builder().dataSource(dataSource()) 
            .connectionFactory(SpringConnFactory.class)
            .config(config -> {
                config.setDbType(DbType.MYSQL)
                .setBatchSize(200)
                .setFetchSize(100)
                .setQueryTimeout(120)
                .setDataCenterId(1L)
                .setWorkerId(1L);
            })
            .mapping(mapper -> {
                // 注册数据库与JAVA映射，按需注册可提高性能
                mapper.register(String.class, new StringPropertyHandler())
                .register(BigDecimal.class, new BigDecimalPropertyHandler())
                // 基础类型和包装类需要分开注册
                .register(Long.class, new LongPropertyHandler())
                .register(long.class, new LongPropertyHandler(true))
                .register(Boolean.class, new BooleanPropertyHandler())
                .register(int.class, new IntegerPropertyHandler(true))
                .register(Date.class, new DatePropertyHandler());
            })
            .build();
    }
}
```
配置 参数：
* dbType: 数据库类型，目前支持 mysql、sqlserver、sqlserver2012；
* showSql: 是否显示 SQL 和 SQL 参数，设置为true则显示，日志级别为info；
* showResult: 是否显示查询结果，设置为true则显示，日志级别为info；
* batchSize: 执行批量插入或更新时，每批次处理数据的数量，默认值为200；
* fetchSize: 执行查询时ResultSet每次抓取数据的数量，默认值为1000；
* queryTimeout: 执行查询的超时时间，默认为60秒；
* maxPageSize: 分页查询时，每页最大记录数，设置小于0时，不限制；
* workerId: 终端ID(雪花算法生成主键用)；
* dataCenterId: 数据中心ID(雪花算法生成主键用)；

### 属性类型解析器

registerPropertyHandler 内置注册的实体属性类型解析器：

```java
register(String.class, new StringPropertyHandler());
register(java.util.Date.class, new DatePropertyHandler());
register(BigDecimal.class, new BigDecimalPropertyHandler());
register(boolean.class, new BooleanPropertyHandler(true));
register(Boolean.class, new BooleanPropertyHandler());
register(Boolean.TYPE, new BooleanPropertyHandler());
register(int.class, new IntegerPropertyHandler(true));
register(Integer.class, new IntegerPropertyHandler());
register(Integer.TYPE, new IntegerPropertyHandler());
register(long.class, new LongPropertyHandler(true));
register(Long.class, new LongPropertyHandler());
register(Long.TYPE, new LongPropertyHandler());
register(UUID.class, new UUIDPropertyHandler());
register(short.class, new ShortPropertyHandler(true));
register(Short.class, new ShortPropertyHandler());
register(Short.TYPE, new ShortPropertyHandler());
register(double.class, new DoublePropertyHandler(true));
register(Double.class, new DoublePropertyHandler());
register(Double.TYPE, new DoublePropertyHandler());
register(float.class, new FloatPropertyHandler(true));
register(Float.class, new FloatPropertyHandler());
register(Float.TYPE, new FloatPropertyHandler());
register(byte.class, new BytePropertyHandler(true));
register(Byte.class, new BytePropertyHandler());
register(Byte.TYPE, new BytePropertyHandler());
register(byte[].class, new ByteArrayPropertyHandler());
register(Byte[].class, new ByteArrayPropertyHandler());
register(Blob.class, new BlobPropertyHandler());
register(Clob.class, new ClobPropertyHandler());
```
### 自定义实体属性类型解析器

我们来看 UUIDPropertyHandler 的实现，只需继承 PropertyHandler 类，实现 ResultSet 类型与目标类型的转换即可：

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
```

### Repository

按照DDD的思想，我们把 Repository 分为 Query 和 Repository，Query 负责实现查询功能，Repository 负责实现业务功能：

#### Query

```java
@org.springframework.stereotype.Repository
public class ProductQuery extends Query {
    @Autowired
    public ProductQuery (@Qualifier("dbTemplate") DBTemplate dbTemplate) {
        super(dbTemplate);
    }
}
```
#### Repository

```java
@org.springframework.stereotype.Repository
public class ProductRepository extends Repository<Product> {
    @Autowired
    public ProductRepository (@Qualifier("dbTemplate") DBTemplate dbTemplate) {
        super(dbTemplate);
    }
}
```


### 代码生成器

可以根据数据库（目前只支持MySQL）结构生成实体类和Repository类，使用方法：
```java
//生成器使用方法
public class GeneratorTest{
  public void test(){
    String url      = "jdbc:mysql://127.0.0.1:3306/myfavs_test?useUnicode=true&useServerPrepStmts=false&rewriteBatchedStatements=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
    String user     = "root";
    String password = "root";
    
    GeneratorConfig             config     = new GeneratorConfig();
    Map<String, TypeDefinition> typeMapper = config.getTypeMapper();
    
    config.setDbType(DbType.MYSQL);                                       //数据库类型
    config.setJdbcUrl(url);                                               //数据库URL
    config.setJdbcUser(user);                                             //数据库用户
    config.setJdbcPwd(password);                                          //数据库密码
    config.setRootPath("D:");                                             //代码输出根目录

    config.setPrefix("tb_");                                              //忽略的表前缀    

    config.setGenEntities(true);                                          //是否生成实体
    config.setCoverEntitiesIfExists(true);                                //实体存在时是否覆盖？
    config.setEntitiesPackage("work.myfavs.framework.example.domain.entity");           //实体Package名称
    
    config.setGenRepositories(true);                                      //是否生成Repository
    config.setCoverRepositoriesIfExists(false);                           //Repository存在时是否覆盖？
    config.setRepositoriesPackage("work.myfavs.framework.example.repository");          //Repository Package名称
    
    //注册生成器类型
    typeMapper.put("varchar", new TypeDefinition("java.lang.String"));
    typeMapper.put("datetime", new TypeDefinition("java.util.Date"));
    typeMapper.put("decimal", new TypeDefinition("java.math.BigDecimal", "BigDecimal.ZERO"));
    typeMapper.put("bigint", new TypeDefinition("java.lang.Long", "long", "0L"));
    typeMapper.put("int", new TypeDefinition("java.lang.Integer", "int", "0"));
    typeMapper.put("bit", new TypeDefinition("java.lang.Boolean", "boolean", "false"));
    
    codeGenerator = new CodeGenerator(config);
    codeGenerator.genEntities();
    codeGenerator.genRepositories();
  }
}
```

如果数据表对应类型使用的是枚举类，需要在数据表注释（#字符）后指定枚举类全类名（枚举类需手动创建）：
```
数据字段注释#work.myfavs.framework.example.domain.enums.TypeEnum
```
