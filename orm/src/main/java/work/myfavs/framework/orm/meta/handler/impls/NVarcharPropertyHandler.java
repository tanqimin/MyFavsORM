/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package work.myfavs.framework.orm.meta.handler.impls;

import work.myfavs.framework.orm.meta.handler.PropertyHandler;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * {@code NVarchar} 类型的属性处理器.
 * <p>处理 Java {@code NVarchar} 类型与 JDBC {@code NVARCHAR} 类型之间的相互转换.</p>
 */
public class NVarcharPropertyHandler extends PropertyHandler<NVarchar> {

  /**
   * 构造 {@code NVarcharPropertyHandler} 实例.
   */
  public NVarcharPropertyHandler() {

  }

  /**
   * 从 ResultSet 中读取值并转换为 {@code NVarchar}.
   *
   * @param rs          ResultSet
   * @param columnIndex 字段索引
   * @param clazz       目标类型
   * @return NVarchar 对象
   * @throws SQLException SQLException
   */
  @Override
  public NVarchar convert(ResultSet rs, int columnIndex, Class<NVarchar> clazz) throws SQLException {

    Object val = rs.getObject(columnIndex);
    if (null == val) {
      return null;
    }

    return new NVarchar(val.toString());
  }

  /**
   * 将 {@code NVarchar} 参数添加到 PreparedStatement.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQLException
   */
  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, NVarchar param) throws SQLException {

    ps.setNString(paramIndex, param.toString());
  }

  /**
   * 获取 SQL 类型代码.
   *
   * @return {@code Types.NVARCHAR}
   */
  @Override
  public int getSqlType() {

    return Types.NVARCHAR;
  }
}
