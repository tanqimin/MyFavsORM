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

    final List<TModel>      result      = new ArrayList<>();
    final ResultSetMetaData metaData    = rs.getMetaData();
    final int               columnCount = metaData.getColumnCount();

    while (rs.next()) {
      TModel model = ReflectUtil.newInstance(modelClass);
      for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {

        String    columnLabel = metaData.getColumnLabel(columnIndex).toUpperCase();
        Attribute attr        = attributes.get(columnLabel);
        if (null == attr) continue;
        attr.setValue(model, rs, columnIndex);
      }
      result.add(model);
    }

    return result;
  }

  private static <TModel> List<TModel> toRecords(Class<TModel> modelClass, ResultSet rs)
      throws SQLException {

    final List<TModel>      result      = new ArrayList<>();
    final ResultSetMetaData metaData    = rs.getMetaData();
    final int               columnCount = metaData.getColumnCount();

    while (rs.next()) {
      TModel tModel = ReflectUtil.newInstance(modelClass);
      for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
        String columnLabel = metaData.getColumnLabel(columnIndex);
        Object colValue    = rs.getObject(columnIndex);
        ((Record) tModel).put(columnLabel, colValue);
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
