package work.myfavs.framework.orm.util.lang;

import java.sql.SQLType;
import java.util.Objects;
import lombok.Setter;

@Setter
public class Unicode implements java.io.Serializable {

  protected String content;

  protected String sqlType;

  public Unicode() {
  }

  public Unicode(String content) {
    this.content = content;
  }

  public Unicode(String content, String sqlType) {
    this.content = content;
    this.sqlType = sqlType;
  }

  public String getContent() {
    return content;
  }

  public String getSqlType() {
    return sqlType;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Unicode unicode = (Unicode) o;
    return Objects.equals(content, unicode.content) && Objects.equals(sqlType, unicode.sqlType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(content, sqlType);
  }

  @Override
  public String toString() {
    return content;
  }
}
