package work.myfavs.framework.orm.meta;

import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.util.common.CollectionUtil;
import work.myfavs.framework.orm.util.common.StringUtil;
import work.myfavs.framework.orm.util.exception.DBException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parameters {
  private final Map<Integer/*paramIndex*/, Object> parameters = new LinkedHashMap<>();

  public void addParameters(Collection<?> params) {
    if (CollectionUtil.isEmpty(params)) return;

    int paramIndex = parameters.size();
    for (Object param : params) {
      parameters.put(++paramIndex, param);
    }
  }

  public void addParameter(int paramIndex, Object param) {
    if (parameters.containsKey(paramIndex))
      throw new DBException("设置参数 %s 时出现异常: 参数索引 %d 已存在! ", StringUtil.toStr(param), paramIndex);

    parameters.put(paramIndex, param);
  }

  public void addParameter(Object param) {
    int paramIndex = parameters.size();
    parameters.put(++paramIndex, param);
  }

  public boolean isEmpty() {
    return parameters.isEmpty();
  }

  @SuppressWarnings("unchecked")
  public void applyParameters(PreparedStatement statement) {
    if (isEmpty()) return;

    try {
      for (Map.Entry<Integer, Object> entry : parameters.entrySet()) {
        Integer paramIndex = entry.getKey();
        Object  value      = entry.getValue();

        if (null == value) {
          statement.setObject(paramIndex, null);
          continue;
        }

        PropertyHandlerFactory
            .getInstance(value.getClass())
            .addParameter(statement, paramIndex, value);
      }
    } catch (SQLException ex) {
      throw new DBException(ex, "设置参数时发生异常: %s", ex.getMessage());
    }
  }

  public Map<Integer, Object> getParameters() {
    return this.parameters;
  }

  public int size() {
    return this.parameters.size();
  }
}
