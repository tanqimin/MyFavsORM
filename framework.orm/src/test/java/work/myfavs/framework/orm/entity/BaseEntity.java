package work.myfavs.framework.orm.entity;

import work.myfavs.framework.orm.entity.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.Criterion;
import work.myfavs.framework.orm.meta.enumeration.Operator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public abstract class BaseEntity implements Serializable {
  public interface Update{}
  /** 创建时间 */
  @Column(value = "created")
  private Date created = null;
  /** 名称 */
  @Column(value = "name")
  @Criterion(value = "name", operator = Operator.LIKE)
  @Criterion(value = "name", operator = Operator.NOT_EQUALS, group = Update.class)
  private String name = null;
  /** 是否停用 */
  @Column(value = "disable")
  private Boolean disable = false;
  /** 价格 */
  @Column(value = "price")
  private BigDecimal price = BigDecimal.ZERO;
  /** 类型 */
  @Column(value = "type")
  @Criterion(order = 2)
  private TypeEnum type = null;
  /** 配置 */
  @Column(value = "config")
  private String config = null;

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
}
