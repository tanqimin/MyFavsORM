package work.myfavs.framework.orm.util.convert;

import cn.hutool.core.util.ReflectUtil;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.Attributes;
import work.myfavs.framework.orm.meta.schema.Metadata;
import work.myfavs.framework.orm.util.common.Constant;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    final List<TModel>      result      = new ArrayList<>();
    final Attributes        attributes  = Metadata.get(modelClass).getQueryAttributes();
    final ResultSetMetaData metaData    = rs.getMetaData();
    final int               columnCount = metaData.getColumnCount();

    while (rs.next()) {
      TModel model = ReflectUtil.newInstance(modelClass);
      for (int i = 1; i <= columnCount; i++) {
        Attribute attr = attributes.getAttribute(metaData.getColumnName(i));
        if (Objects.isNull(attr)) continue;
        attr.getFieldVisitor().setValue(model, attr.value(rs));
      }
      result.add(model);
    }

    return result;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static <TModel> List<TModel> toScalar(Class<TModel> modelClass, ResultSet rs)
      throws SQLException {

    final List<TModel>      list     = new ArrayList<>();
    final ResultSetMetaData metaData = rs.getMetaData();

    PropertyHandler propertyHandler = PropertyHandlerFactory.getInstance(modelClass);
    while (rs.next()) {
      String colName = metaData.getColumnLabel(1);
      list.add((TModel) propertyHandler.convert(rs, colName, modelClass));
    }
    return list;
  }

  private static <TModel> List<TModel> toRecords(Class<TModel> modelClass, ResultSet rs)
      throws SQLException {

    final List<TModel>      list        = new ArrayList<>();
    final ResultSetMetaData metaData    = rs.getMetaData();
    final int               columnCount = metaData.getColumnCount();

    while (rs.next()) {
      TModel tModel = ReflectUtil.newInstance(modelClass);
      for (int i = 1; i <= columnCount; i++) {
        String colName  = metaData.getColumnLabel(i);
        Object colValue = rs.getObject(i);
        ((Record) tModel).put(colName, rs.wasNull() ? null : colValue);
      }
      list.add(tModel);
    }

    return list;
  }
}
