package work.myfavs.framework.orm.util.convert;

import cn.hutool.core.util.ReflectUtil;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.meta.schema.Attribute;
import work.myfavs.framework.orm.meta.schema.Attributes;
import work.myfavs.framework.orm.meta.schema.Metadata;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库类型转换
 */
public class DBConvert {

  private static final List<Class<?>> primitives = List.of(Integer.class,
                                                           Long.class,
                                                           Double.class,
                                                           String.class,
                                                           Float.class,
                                                           Boolean.class,
                                                           Short.class);

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
    if (modelClass.isPrimitive() || primitives.contains(modelClass)) {
      return toScalar(modelClass, rs);
    }

    return toEntity(modelClass, rs);
  }

  private static <TModel> List<TModel> toEntity(
      Class<TModel> modelClass, ResultSet rs) throws SQLException {

    final List<TModel>      list        = new ArrayList<>();
    final ResultSetMetaData metaData    = rs.getMetaData();
    final int               columnCount = metaData.getColumnCount();
    final Attributes        attributes  = Metadata.get(modelClass).getQueryAttributes();

    while (rs.next()) {
      TModel model = ReflectUtil.newInstance(modelClass);
      for (int i = 1; i < columnCount + 1; i++) {
        String colName = metaData.getColumnLabel(i);
        if (!attributes.containsColumn(colName)) continue;
        Attribute attr = attributes.getAttribute(colName);
        ReflectUtil.setFieldValue(model, attr.getFieldName(), attr.value(rs));
      }
      list.add(model);
    }
    return list;
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

  public static List<Record> toRecords(ResultSet rs) throws SQLException {
    final List<Record>      list        = new ArrayList<>();
    final ResultSetMetaData metaData    = rs.getMetaData();
    final int               columnCount = metaData.getColumnCount();

    while (rs.next()) {
      Record record = new Record();
      for (int i = 1; i <= columnCount; i++) {
        String colName  = metaData.getColumnLabel(i);
        Object colValue = rs.getObject(colName);
        record.put(colName, rs.wasNull() ? null : colValue);
      }
      list.add(record);
    }

    return list;
  }
}
