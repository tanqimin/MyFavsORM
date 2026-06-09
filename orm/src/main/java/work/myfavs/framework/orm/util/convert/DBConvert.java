package work.myfavs.framework.orm.util.convert;

import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.ClassMeta;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.Constant;
import work.myfavs.framework.orm.util.reflection.ReflectUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
  public static <TModel> List<TModel> toList(Class<TModel> modelClass, ResultSet rs)
      throws SQLException {

    if (modelClass == Record.class) {
      return toRecords(modelClass, rs);
    }

    if (modelClass.isPrimitive() || Constant.PRIMITIVE_TYPES.contains(modelClass)) {
      return toScalar(modelClass, rs);
    }

    return toEntities(modelClass, rs);
  }

  private static <TModel> List<TModel> toEntities(
      Class<TModel> modelClass, ResultSet rs) throws SQLException {

    ClassMeta                                   classMeta  = Metadata.classMeta(modelClass);
    final Map<String /*columnName*/, Attribute> attributes = classMeta.getQueryAttributes();

    final ResultSetMetaData metaData    = rs.getMetaData();
    final int               columnCount = metaData.getColumnCount();

    // 缓存列标签（大写）到数组，避免每行重复调用 getColumnLabel + toUpperCase
    final String[] columnLabels = new String[columnCount];
    for (int i = 0; i < columnCount; i++) {
      columnLabels[i] = metaData.getColumnLabel(i + 1).toUpperCase();
    }

    final List<TModel> result = new ArrayList<>();
    while (rs.next()) {
      TModel model = ReflectUtil.newInstance(modelClass);
      for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
        Attribute attr = attributes.get(columnLabels[columnIndex]);
        if (null == attr) continue;
        attr.setValue(model, rs, columnIndex + 1);
      }
      result.add(model);
    }

    return result;
  }

  private static <TModel> List<TModel> toRecords(Class<TModel> modelClass, ResultSet rs)
      throws SQLException {

    final ResultSetMetaData metaData    = rs.getMetaData();
    final int               columnCount = metaData.getColumnCount();

    // 缓存列标签到数组，避免每行重复调用 getColumnLabel
    final String[] columnLabels = new String[columnCount];
    for (int i = 0; i < columnCount; i++) {
      columnLabels[i] = metaData.getColumnLabel(i + 1);
    }

    final List<TModel> result = new ArrayList<>();
    while (rs.next()) {
      TModel tModel = ReflectUtil.newInstance(modelClass);
      for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
        Object colValue = rs.getObject(columnIndex + 1);
        ((Record) tModel).put(columnLabels[columnIndex], colValue);
      }
      result.add(tModel);
    }

    return result;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <TModel> List<TModel> toScalar(Class<TModel> modelClass, ResultSet rs)
      throws SQLException {

    final List<TModel> list = new ArrayList<>();

    PropertyHandler propertyHandler = PropertyHandlerFactory.getInstance(modelClass);
    while (rs.next()) {
      list.add((TModel) propertyHandler.convert(rs, 1, modelClass));
    }
    return list;
  }
}
