package work.myfavs.framework.orm.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import work.myfavs.framework.orm.meta.schema.AttributeMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.repository.handler.PropertyHandlerFactory;

/**
 * 数据库类型转换
 */
@Slf4j
public class DBConvert {

  /**
   * 把ResultSet转换为指定类型的List
   *
   * @param modelClass Class
   * @param rs         ResultSet
   * @param <TModel>   Class TModel
   *
   * @return List
   */
  public static <TModel> List<TModel> toList(Class<TModel> modelClass, ResultSet rs)
      throws SQLException {

    final List<TModel>               result;
    final Map<String, AttributeMeta> attrMetas;
    final ResultSetMetaData          rsmd;
    final int                        colCount;
    final boolean                    queryScalar;

    result = new LinkedList<>();
    rsmd = rs.getMetaData();
    attrMetas = Metadata.get(modelClass).getQueryAttributes();
    colCount = rsmd.getColumnCount();
    queryScalar = attrMetas.isEmpty() && colCount == 1;

    String colName;
    if (queryScalar) {
      while (rs.next()) {
        colName = rsmd.getColumnLabel(1);
        result.add(PropertyHandlerFactory.convert(rs, colName, modelClass));
      }
      return result;
    }

    AttributeMeta attributeMeta;
    Object        columnValue;
    try {
      while (rs.next()) {
        TModel model = ReflectUtil.newInstance(modelClass);
        for (int i = 1;
             i <= colCount;
             i++) {
          colName = rsmd.getColumnLabel(i);
          attributeMeta = attrMetas.get(colName.toUpperCase());
          if (attributeMeta == null) {
            continue;
          }
          columnValue = PropertyHandlerFactory.convert(rs, colName, attributeMeta.getFieldType());
          BeanUtils.setProperty(model, attributeMeta.getFieldName(), columnValue);
        }
        result.add(model);
      }
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error(e.getMessage(), e);
    }

    return result;
  }

}
