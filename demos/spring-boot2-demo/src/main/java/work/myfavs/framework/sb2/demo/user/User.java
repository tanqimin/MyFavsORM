package work.myfavs.framework.sb2.demo.user;

import work.myfavs.framework.orm.meta.annotation.Column;
import work.myfavs.framework.orm.meta.annotation.PrimaryKey;
import work.myfavs.framework.orm.meta.annotation.Table;
import work.myfavs.framework.orm.meta.enumeration.GenerationType;
import work.myfavs.framework.orm.util.lang.NVarchar;
import work.myfavs.framework.sb2.demo.user.enums.UserTypeEnum;

import java.util.Date;

/**
 * 用户实体，映射 {@code tb_user} 表，使用雪花算法主键生成策略.
 */
@Table(value = "tb_user", strategy = GenerationType.SNOW_FLAKE)
public class User {
  @Column
  @PrimaryKey
  private Long    id;
  @Column
  private Date    created;
  @Column
  private Date     modified;
  @Column
  private NVarchar username;
  @Column
  private String   email;
  @Column
  private String  password;

  @Column(value = "user_type")
  private UserTypeEnum userType;

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

  public Date getModified() {
    return modified;
  }

  public void setModified(Date modified) {
    this.modified = modified;
  }

  public NVarchar getUsername() {
    return username;
  }

  public void setUsername(NVarchar username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserTypeEnum getUserType() {
    return userType;
  }

  public void setUserType(UserTypeEnum userType) {
    this.userType = userType;
  }
}
