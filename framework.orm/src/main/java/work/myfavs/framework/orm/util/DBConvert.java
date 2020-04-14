package work.myfavs.framework.orm.util;

import cn.hutool.core.util.ReflectUtil;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.handler.PropertyHandlerFactory;
import work.myfavs.framework.orm.meta.schema.AttributeMeta;
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
    return toScalarStream(modelClass, rs).collect(Collectors.toList());
  }

  public static <TModel> Stream<TModel> toStream(Class<TModel> modelClass, ResultSet rs)
      throws SQLException {
    final Map<String, AttributeMeta> attrMetas;

    if (modelClass == Record.class) {
      return toRecordStream(modelClass, rs);
    }

    attrMetas = Metadata.get(modelClass)
        .getQueryAttributes();

    if (attrMetas.isEmpty() && rs.getMetaData()
        .getColumnCount() == 1) {
      return toScalarStream(modelClass, rs);
    }

    return toEntityStream(modelClass, rs, attrMetas);
  }

  private static <TModel> Stream<TModel> toEntityStream(Class<TModel> modelClass,
      ResultSet rs,
      Map<String, AttributeMeta> attrMetas)
      throws SQLException {

    final Builder<TModel> builder;

    final ResultSetMetaData metaData;
    final int columnCount;

    TModel model;
    String colName;
    Object columnValue;
    AttributeMeta attributeMeta;

    builder = Stream.builder();
    metaData = rs.getMetaData();
    columnCount = metaData.getColumnCount();

    while (rs.next()) {
      model = ReflectUtil.newInstance(modelClass);
      for (int i = 1;
          i <= columnCount;
          i++) {
        colName = metaData.getColumnLabel(i)
            .toUpperCase();
        if (!attrMetas.containsKey(colName)) {
          continue;
        }
        attributeMeta = attrMetas.get(colName);
        columnValue = attributeMeta.convert(rs);
        ReflectUtil.setFieldValue(model, attributeMeta.getFieldName(), columnValue);
      }
      builder.add(model);
    }

    return builder.build();
  }

  private static <TModel> Stream<TModel> toScalarStream(Class<TModel> modelClass,
      ResultSet rs)
      throws SQLException {

    final Builder<TModel> builder;

    final ResultSetMetaData metaData;
    String colName;

    builder = Stream.builder();
    metaData = rs.getMetaData();

    while (rs.next()) {
      colName = metaData.getColumnLabel(1)
          .toUpperCase();
      builder.add(PropertyHandlerFactory.convert(rs, colName, modelClass));
    }
    return builder.build();
  }

  private static <TModel> Stream<TModel> toRecordStream(Class<TModel> modelClass,
      ResultSet rs)
      throws SQLException {

    final Builder<TModel> builder;

    final ResultSetMetaData metaData;
    final int columnCount;

    TModel tModel;
    String colName;
    Object colValue;

    builder = Stream.builder();
    metaData = rs.getMetaData();
    columnCount = metaData.getColumnCount();

    while (rs.next()) {
      tModel = ReflectUtil.newInstance(modelClass);
      for (int i = 1;
          i <= columnCount;
          i++) {
        colName = metaData.getColumnLabel(i);
        colValue = rs.getObject(i);
        ((Record) tModel).put(colName, rs.wasNull()
            ? null
            : colValue);
      }
      builder.add(tModel);
    }

    return builder.build();
  }

}
