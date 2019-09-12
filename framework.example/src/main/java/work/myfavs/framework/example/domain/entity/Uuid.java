package work.myfavs.framework.example.domain.entity;

import java.io.Serializable;
import lombok.Data;
import java.util.Objects;
import java.lang.Boolean;
import java.lang.String;
import java.math.BigDecimal;
import java.util.Date;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;

/**
 * Uuid 实体类
 * PS: 此文件通过代码生成器生成，修改此文件会有被覆盖的风险
 */
@Data
@Table(value = Uuid.META.TABLE, strategy = GenerationType.UUID)
public class Uuid
    implements Serializable {

  /**
   * ID
   */
  @Column(value = Uuid.META.COLUMNS.id)
  @PrimaryKey
  private String id = null;
  /**
   * 创建时间
   */
  @Column(value = Uuid.META.COLUMNS.created)
  private Date created = null; 
  /**
   * 名称
   */
  @Column(value = Uuid.META.COLUMNS.name)
  private String name = null; 
  /**
   * 是否停用
   */
  @Column(value = Uuid.META.COLUMNS.disable)
  private Boolean disable = false; 
  /**
   * 价格
   */
  @Column(value = Uuid.META.COLUMNS.price)
  private BigDecimal price = BigDecimal.ZERO; 
  /**
   * 类型
   */
  @Column(value = Uuid.META.COLUMNS.type)
  private TypeEnum type = null; 

  /**
   * 元数据
   */
  public enum META {
    ;
    /**
     * 表名
     */
    public static final String TABLE = "tb_uuid";
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
       * 是否停用
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
    Uuid entity = (Uuid) o;
    return id.equals(entity.id);
  }

  @Override
  public int hashCode() {

    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Uuid { "
      + "id = " + id  + ", "
      + "created = " + created  + ", "
      + "name = " + name  + ", "
      + "disable = " + disable  + ", "
      + "price = " + price  + ", "
      + "type = " + type 
      + " }";
  }
}