package work.myfavs.framework.orm.util.convert;

import cn.hutool.core.util.ReflectUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.Attributes;
import work.myfavs.framework.orm.meta.schema.Metadata;

/**
 * 数据库类型转换
 */
public class DBConvert {

  /**
   * 把ResultSet转换为指定类型的List
   *
   * @param modelClass Class
   * @param rs         ResultSet
   * @param <TModel>   Class TModel
   * @return List
   * @throws SQLException SQLException
   */
  public static <TModel> List<TModel> toList(Class<TModel> modelClass,
      ResultSet rs)
      throws SQLException {

    if (modelClass == Record.class) {
      return toRecord(modelClass, rs);
    }

    final Attributes attrMetas = Metadata.get(modelClass)
        .getQueryAttributes();

    if (attrMetas.isEmpty() && rs.getMetaData()
        .getColumnCount() == 1) {
      return toScalar(modelClass, rs);
    }

    return toEntity(modelClass, rs, attrMetas);
  }

  private static <TModel> List<TModel> toEntity(Class<TModel> modelClass,
      ResultSet rs,
      Attributes attributes)
      throws SQLException {

    final List<TModel> list = new ArrayList<>();
    final ResultSetMetaData metaData = rs.getMetaData();
    final int columnCount = metaData.getColumnCount();

    //找出与查询结果匹配的字段
    final List<Attribute> existsAttrs = new ArrayList<>();
    for (int i = 1; i <= columnCount; i++) {
      if (attributes.containsColumn(metaData.getColumnLabel(i))) {
        existsAttrs.add(attributes.getAttribute(metaData.getColumnLabel(i)));
      }
    }

    while (rs.next()) {
      TModel model = ReflectUtil.newInstance(modelClass);
      for (Attribute attr : existsAttrs) {
        ReflectUtil.setFieldValue(model, attr.getFieldName(), attr.value(rs));
      }
      list.add(model);
    }

    return list;
  }

  private static <TModel> List<TModel> toScalar(Class<TModel> modelClass,
      ResultSet rs)
      throws SQLException {

    final List<TModel> list = new ArrayList<>();
    final ResultSetMetaData metaData = rs.getMetaData();

    while (rs.next()) {
      String colName = metaData.getColumnLabel(1);
      list.add(PropertyHandlerFactory.convert(rs, colName, modelClass));
    }
    return list;
  }

  private static <TModel> List<TModel> toRecord(Class<TModel> modelClass,
      ResultSet rs)
      throws SQLException {

    final List<TModel> list = new ArrayList<>();
    final ResultSetMetaData metaData = rs.getMetaData();
    final int columnCount = metaData.getColumnCount();

    while (rs.next()) {
      TModel tModel = ReflectUtil.newInstance(modelClass);
      for (int i = 1; i <= columnCount; i++) {
        String colName = metaData.getColumnLabel(i);
        Object colValue = rs.getObject(i);
        ((Record) tModel).put(colName, rs.wasNull() ? null : colValue);
      }
      list.add(tModel);
    }

    return list;
  }

}
