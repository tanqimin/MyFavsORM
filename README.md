[![GitHub release](https://img.shields.io/github/stars/tanqimin/myfavs.framework?style=flat-square)](https://github.com/tanqimin/myfavs.framework)
# MyFavs ORM
##### A simple ORM Framework base on Spring Framework
如果您厌倦了MyBatis复杂的 XML 语法，并且擅长 SQL 的编写，可以试试 MyFavs ORM。
## Framework configuration
```java
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource datesource(){
        //创建DataSource
    }

    @Bean
    public DBTemplate dbTemplate(){
        return DBTemplate.build(dataSource())
                     //设置使用的数据库类型
                     .setDbType(DbType.MYSQL)
                     //注册实体属性类型解析器，可自定义扩展
                     .registerPropertyHandler(String.class, new StringPropertyHandler())
                     .registerPropertyHandler(BigDecimal.class, new BigDecimalPropertyHandler())
                     .registerPropertyHandler(Long.class, new LongPropertyHandler())
                     .registerPropertyHandler(Boolean.class, new BooleanPropertyHandler())
                     .registerPropertyHandler(LocalDateTime.class, new LocalDateTimePropertyHandler());
    }
}
```
DBTemplate 参数：
* dbType: 数据库类型，目前支持 mysql、sqlserver、sqlserver2012；
* showSql: 是否显示 SQL 和 SQL 参数，设置为true则显示，日志级别为info；
* showResult: 是否显示查询结果，设置为true则显示，日志级别为info；
* batchSize: 执行批量插入或更新时，每批次处理数据的数量，默认值为200；
* fetchSize: 执行查询时ResultSet每次抓取数据的数量，默认值为1000；
* queryTimeout: 执行查询的超时时间，默认为60秒；
* maxPageSize: 分页查询时，每页最大记录数，设置小于0时，不限制；
* workerId: 终端ID(雪花算法生成主键用)；
* dataCenterId: 数据中心ID(雪花算法生成主键用)；
* transactionIsolation: 事务隔离级别，默认为 Connection.TRANSACTION_READ_UNCOMMITTED

registerPropertyHandler 内置注册的实体属性类型解析器：
```
    registerPropertyHandler(String.class, new StringPropertyHandler());
    registerPropertyHandler(java.util.Date.class, new DatePropertyHandler());
    registerPropertyHandler(LocalDateTime.class, new LocalDateTimePropertyHandler());
    registerPropertyHandler(LocalDate.class, new LocalDatePropertyHandler());
    registerPropertyHandler(LocalTime.class, new LocalTimePropertyHandler());
    registerPropertyHandler(BigDecimal.class, new BigDecimalPropertyHandler());
    registerPropertyHandler(Boolean.class, new BooleanPropertyHandler());
    registerPropertyHandler(Boolean.TYPE, new BooleanPropertyHandler());
    registerPropertyHandler(Integer.class, new IntegerPropertyHandler());
    registerPropertyHandler(Integer.TYPE, new IntegerPropertyHandler());
    registerPropertyHandler(Long.class, new LongPropertyHandler());
    registerPropertyHandler(Long.TYPE, new LongPropertyHandler());
    registerPropertyHandler(UUID.class, new UUIDPropertyHandler());
    registerPropertyHandler(Short.class, new ShortPropertyHandler());
    registerPropertyHandler(Short.TYPE, new ShortPropertyHandler());
    registerPropertyHandler(Double.class, new DoublePropertyHandler());
    registerPropertyHandler(Double.TYPE, new DoublePropertyHandler());
    registerPropertyHandler(Float.class, new FloatPropertyHandler());
    registerPropertyHandler(Float.TYPE, new FloatPropertyHandler());
    registerPropertyHandler(Byte.class, new BytePropertyHandler());
    registerPropertyHandler(Byte.TYPE, new BytePropertyHandler());
    registerPropertyHandler(byte[].class, new ByteArrayPropertyHandler());
    registerPropertyHandler(Byte[].class, new ByteArrayPropertyHandler());
    registerPropertyHandler(Blob.class, new BlobPropertyHandler());
    registerPropertyHandler(Clob.class, new ClobPropertyHandler());
```
自定义实体属性类型解析器，我们来看 LocalDateTimePropertyHandler 的实现，只需继承 PropertyHandler 类，实现 ResultSet 类型与目标类型的转换即可：
```java
public class LocalDateTimePropertyHandler extends PropertyHandler<LocalDateTime> {
    @Override
    public LocalDateTime convert(ResultSet rs, String columnName, Class<LocalDateTime> clazz) throws SQLException {
        Timestamp val = rs.getTimestamp(columnName);
        if (rs.wasNull()) {
          return null;
        }
        return val.toLocalDateTime();
    }

    @Override
    public void addParameter(PreparedStatement ps, int paramIndex, LocalDateTime param) throws SQLException {
        ps.setTimestamp(paramIndex, param == null
            ? null
            : Timestamp.valueOf(param));
    }
}
```

## Entity Class
```java
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

@Data
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
}
```
此处用到@Table、@Column和@PrimaryKey三个注解：
+ @Table，定义实体关联的数据表，其中参数value为数据表名称，strategy为主键策略；
   * GenerationType.UUID，UUID，值由系统字段生成
   * GenerationType.SNOW_FLAKE，雪花算法生成，由程序生成字段
   * GenerationType.IDENTITY，数据库自增，值由数据库生成
   * GenerationType.ASSIGNED，自然主键，值由用户自定义
+ @Column，定义实体类关联的数据表字段，参数value为数据表字段名称，如果数据表字段名称与实体属性名称一致，可以忽略；参数readOnly默认值为false，当设置为true时，插入和更新操作不会包含该字段；
+ @PrimaryKey，定义主键关联的属性；

### Repository Class

按照DDD的思想，我们把 Repository 分为 Query 和 Repository，Query 负责实现查询功能，Repository 负责实现业务功能：
Query 类
```java
@org.springframework.stereotype.Repository
public class ProductQuery extends Query {
    @Autowired
    public ProductQuery (@Qualifier("dbTemplate") DBTemplate dbTemplate) {
        super(dbTemplate);
    }
}
```
Repository 类
```java
@org.springframework.stereotype.Repository
public class ProductRepository extends Repository<Product> {
    @Autowired
    public ProductRepository (@Qualifier("dbTemplate") DBTemplate dbTemplate) {
        super(dbTemplate);
    }
}
```
### Query Usage
此处引入一个SQL构建器类：work.myfavs.framework.orm.meta.clause.Sql
```
Sql sql = new Sql("SELECT * FROM tb_product");
//或者
Sql sql = Sql.Select("*").from("tb_product");
```
大多数的查询功能，都会根据前端用户空间选择的值作为筛选条件，当用户没有选择指定的属性时，会忽略此条件
```
String param = "%cake%";
Sql sql = new Sql("SELECT * FROM tb_product").where().and(Cond.like("name", param));
//此处生成的SQL为: SELECT * FROM tb_product WHERE 1 = 1 AND name LIKE '%cake%'
//如果 param 没有包含任何的查询通配符(%和_)，SQL则会优化为: SELECT * FROM tb_product WHERE 1 = 1 AND name = 'cake'
//如果 param 为null，则忽略该条件，SQL为: SELECT * FROM tb_product WHERE 1 = 1
```
##### Query List
在 ProductQuery 类中添加方法 findByName
```java
@org.springframework.stereotype.Repository
public class ProductQuery extends Query {
    //此处省略部分代码    

    public List<Product> findByName(String name){
        //此处需要注意所有条件值被忽略时造成的性能问题
        Sql sql = new Sql("SELECT * FROM tb_product").where().and(Cond.like("name",name));
        return find(Product.class, sql);
    }
}
```
##### Query Page
接下来，我们看看分页是如何实现的，在 ProductQuery 类中添加方法 findPageByName
```java
@org.springframework.stereotype.Repository
public class ProductQuery extends Query {
    //此处省略部分代码   
    public Page<Product> findPageByName(String name, long currentPage, long pageSize){
        //此处需要注意所有条件值被忽略时造成的性能问题
        Sql sql = new Sql("SELECT * FROM tb_product").where().and(Cond.like("name",name));
        return findPage(Product.class, sql, true, currentPage, pageSize);
    }
}
```
##### Use Record Class
在开发一些小项目的时候，可以使用 Record 作为返回类型，避免创建大量的 VO 对象
```java
@org.springframework.stereotype.Repository
public class ProductQuery extends Query {
    //此处省略部分代码    

    public List<Record> findRecordsByName(String name){
        //此处需要注意所有条件值被忽略时造成的性能问题
        Sql sql = new Sql("SELECT * FROM tb_product").where().and(Cond.like("name",name));
        return find(Record.class, sql);
    }

    public Page<Record> findRecordsPageByName(String name, long currentPage, long pageSize){
        //此处需要注意所有条件值被忽略时造成的性能问题
        Sql sql = new Sql("SELECT * FROM tb_product").where().and(Cond.like("name",name));
        return findPage(Record.class, sql, true, currentPage, pageSize);
    }

}
```
### Repository Usage
定义 ProductService 类 create 方法，实现批量新增数据
```java
@Service
public class ProductService{
   @Autowired
    private ProductRepository productRepository;
    
    @Transactional 
    public int batchCreate(List<ProductDto> dtoList){
        List<Product> products = new ArrayList<>();
        for(ProductDto dto : dtoList){
            Product product = new Product();
            product.setCreated(dto.getCreated());
            product.setName(dto.getName());
            product.setDisable(dto.disable());
            product.setPrice(dto.getPrice());
            products.add(product);
        }
        return productRepository.create(products);
    }
}
```