package work.myfavs.framework.example.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

/** Snowfake 实体类 PS: 此文件通过代码生成器生成，修改此文件会有被覆盖的风险 */
@Table(value = Snowflake.META.TABLE, strategy = GenerationType.SNOW_FLAKE)
public class Snowflake implements Serializable {

  /** ID */
  @Column(value = Snowflake.META.COLUMNS.id)
  @PrimaryKey
  private Long id = null;
  /** 创建时间 */
  @Column(value = Snowflake.META.COLUMNS.created)
  private Date created = null;
  /** 名称 */
  @Column(value = Snowflake.META.COLUMNS.name)
  private String name = null;
  /** 是否停用 */
  @Column(value = Snowflake.META.COLUMNS.disable)
  private Boolean disable = false;
  /** 价格 */
  @Column(value = Snowflake.META.COLUMNS.price)
  private BigDecimal price = BigDecimal.ZERO;
  /** 类型 */
  @Column(value = Snowflake.META.COLUMNS.type)
  private TypeEnum type = null;
  /** 配置 */
  @Column(value = Snowflake.META.COLUMNS.config)
  private String config = null;

  public Long getId() {

    return id;
  }

  public void setId(Long id) {

    this.id = id;
  }

  public Date getCreated() {

    return created;
  }

  public void setCreated(Date created) {

    this.created = created;
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public Boolean getDisable() {

    return disable;
  }

  public void setDisable(Boolean disable) {

    this.disable = disable;
  }

  public BigDecimal getPrice() {

    return price;
  }

  public void setPrice(BigDecimal price) {

    this.price = price;
  }

  public TypeEnum getType() {

    return type;
  }

  public void setType(TypeEnum type) {

    this.type = type;
  }

  public String getConfig() {

    return config;
  }

  public void setConfig(String config) {

    this.config = config;
  }

  /** 元数据 */
  public enum META {
    ;
    /** 表名 */
    public static final String TABLE = "tb_snowflake";

    /** 字段 */
    public interface COLUMNS {

      /** ID */
      String id = "id";
      /** 创建时间 */
      String created = "created";
      /** 名称 */
      String name = "name";
      /** 是否停用 */
      String disable = "disable";
      /** 价格 */
      String price = "price";
      /** 类型 */
      String type = "type";
      /** 配置 */
      String config = "config";
    }
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Snowflake entity = (Snowflake) o;
    return id.equals(entity.id);
  }

  @Override
  public int hashCode() {

    return Objects.hash(id);
  }

  @Override
  public String toString() {

    return "Snowfake { "
        + "id = "
        + id
        + ", "
        + "created = "
        + created
        + ", "
        + "name = "
        + name
        + ", "
        + "disable = "
        + disable
        + ", "
        + "price = "
        + price
        + ", "
        + "type = "
        + type
        + ", "
        + "config = "
        + config
        + " }";
  }
}
