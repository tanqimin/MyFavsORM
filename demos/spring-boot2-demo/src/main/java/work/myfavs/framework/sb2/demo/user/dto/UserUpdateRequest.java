package work.myfavs.framework.sb2.demo.user.dto;

/**
 * 用户更新请求 DTO，仅包含前端可修改的字段.
 */
public class UserUpdateRequest {

  private String username;
  private String password;
  private String email;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
