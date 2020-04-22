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

/**
 * Identity 实体类 PS: 此文件通过代码生成器生成，修改此文件会有被覆盖的风险
 */
@Table(value = Identity.META.TABLE, strategy = GenerationType.SNOW_FLAKE, logicalDeleteField = "deleted_tag")
public class Identity
    implements Serializable {

  /**
   * ID
   */
  @Column(value = Identity.META.COLUMNS.id)
  @PrimaryKey
  private Long id = null;
  /**
   * 创建时间
   */
  @Column(value = Identity.META.COLUMNS.created)
  private Date created = null;
  /**
   * 名称
   */
  @Column(value = Identity.META.COLUMNS.name)
  private String name = null;
  /**
   * 是否停用？
   */
  @Column(value = Identity.META.COLUMNS.disable)
  private Boolean disable = false;
  /**
   * 价格
   */
  @Column(value = Identity.META.COLUMNS.price)
  private BigDecimal price = BigDecimal.ZERO;
  /**
   * 类型
   */
  @Column(value = Identity.META.COLUMNS.type)
  private TypeEnum type = null;

  @Column(value = Identity.META.COLUMNS.deletedTag)
  private Long deletedTag;

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

  public Long getDeletedTag() {
    return deletedTag;
  }

  public void setDeletedTag(Long deletedTag) {
    this.deletedTag = deletedTag;
  }

  /**
   * 元数据
   */
  public enum META {
    ;
    /**
     * 表名
     */
    public static final String TABLE = "tb_identity";

    /**
     * 字段
     */
    public interface COLUMNS {

      /**
       * ID
       */
      String id = "id";
      /**
       * 创建时间
       */
      String created = "created";
      /**
       * 名称
       */
      String name = "name";
      /**
       * 是否停用？
       */
      String disable = "disable";
      /**
       * 价格
       */
      String price = "price";
      /**
       * 类型
       */
      String type = "type";
      /**
       * 逻辑删除标记
       */
      String deletedTag = "deleted_tag";
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
    Identity entity = (Identity) o;
    return id.equals(entity.id);
  }

  @Override
  public int hashCode() {

    return Objects.hash(id);
  }

  @Override
  public String toString() {

    return "Identity { " + "id = " + id + ", " + "created = " + created + ", " + "name = " + name
        + ", " + "disable = " + disable + ", " + "price = " + price + ", " + "type = " + type
        + " }";
  }

}