# MyFavs ORM — AGENTS.md

## 项目概述

轻量级 Java ORM 框架，偏好手写 SQL。Maven 多模块项目：
- `orm` — 核心库（JDBC 封装、ORM 映射、SQL 构建器）
- `spring-boot-starter/orm-spring-boot2-starter` — Spring Boot 2.x 集成（Repository/Query 基类）
- `demos/spring-boot2-demo` — Spring Boot 2.7.18 示例应用

## 技术栈

- Java 11, JUnit 4（不是 5）, Mockito, HikariCP
- Spring Boot 2.7.18, spring-jdbc 5.3.39（starter 模块可选依赖）

## 构建命令

```bash
# 全量构建（跳过 demo 的发布相关插件）
mvn clean install -DskipTests

# 运行测试（需要 SQL Server，见下方说明）
mvn test -pl orm

# 只编译 orm 模块
mvn compile -pl orm
```

## 测试须知

- 测试继承 `AbstractTest`，**硬编码连接 SQL Server** `192.168.8.246:1433`（库 `myfavs_master`）
- 建表 SQL 在 `orm/src/test/resources/sql/mssql/myfavs_master.sql`
- 测试基类在 `orm/src/test/java/.../AbstractTest.java`：每次执行 `@BeforeClass` 会重建表
- 测试需要真实数据库，无法离线执行

## 核心入口

| 类 | 职责 |
|---|---|
| `DBTemplate` | 全局配置入口（Builder 模式），管理数据源、连接工厂、主键生成器 |
| `Database` | 连接+事务范围（`AutoCloseable`），通过 `dbTemplate.createDatabase()` 创建 |
| `Query` | JDBC 原始查询封装，支持参数绑定、批量操作 |
| `Orm` | 实体级 CRUD，通过 `database.createOrm()` 创建 |
| `Sql` | SQL 构建器，配合 `Cond` 条件构建 |
| `DBConfig` | 数据库类型、showSql、batchSize 等配置 |

## 关键约定

- 字段/表名默认策略：驼峰转下划线小写（`productCode` → `product_code`）
- 主键策略：`GenerationType.UUID`, `SNOW_FLAKE`, `IDENTITY`, `ASSIGNED`
- `PropertyHandler` 需显式注册以提高性能（基础类型和包装类分开注册）
- 数据库类型支持：MYSQL, SQL_SERVER, SQL_SERVER_2012, POSTGRE_SQL, ORACLE, H2（各有对应的 `Orm` 实现）
- 同构表（分表）操作通过 `TableAlias` 实现

## Spring Boot Starter 模式

- 继承 `Repository<T>` 或 `Query` 基类（都在 `orm-spring-boot2-starter` 中）
- 通过 `SpringConnFactory` 与 Spring 事务集成
- 默认使用 `@Qualifier("dbTemplate")` 注入 `DBTemplate`

## 代码生成器

`CodeGenerator` 提供实体和 Repository 代码生成（目前仅支持 MySQL）。

## Maven Central 发布

- SCM: `git://github.com:tanqimin/MyFavsORM`
- 发布到 `oss.sonatype.org`（snapshots + staging）
- GPG 签名、source jar、javadoc jar 在 `verify` 阶段
- demo 模块通过 `<skip>true</skip>` 排除在发布之外

## 文件风格

- 所有 getter/setter/构造器均为手写，无任何代码生成工具依赖
- 不写 Javadoc 注释
- 测试类扩展 `AbstractTest`，使用 JUnit 4 的 `@Test` 和 `Assert`
- 无 Checkstyle/Spotless/Formatter 配置，无 CI 流水线
