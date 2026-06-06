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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * {@code Short} 类型的属性处理器.
 * <p>处理 Java {@code Short} 类型与 JDBC {@code SMALLINT} 类型之间的相互转换.</p>
 */
public class ShortPropertyHandler extends NumberPropertyHandler<Short> {

  /**
   * 构造 {@code ShortPropertyHandler} 实例.
   */
  public ShortPropertyHandler() {
  }

  /**
   * 构造 {@code ShortPropertyHandler} 实例.
   *
   * @param isPrimitive 是否基本类型
   */
  public ShortPropertyHandler(boolean isPrimitive) {
    super(isPrimitive);
  }

  /**
   * 将 {@code Number} 转换为 {@code Short}.
   *
   * @param val Number 值
   * @return Short 值
   */
  @Override
  protected Short convertNumber(Number val) {
    return val.shortValue();
  }

  /**
   * 将 {@code String} 转换为 {@code Short}.
   *
   * @param val 字符串值
   * @return Short 值
   */
  @Override
  protected Short convertString(String val) {
    return Short.parseShort(val);
  }

  /**
   * 设置 JDBC 参数.
   *
   * @param ps         PreparedStatement
   * @param paramIndex 参数索引
   * @param param      参数值
   * @throws SQLException SQLException
   */
  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, Short param) throws SQLException {
    ps.setShort(paramIndex, param);
  }

  /**
   * 获取 SQL 类型代码.
   *
   * @return {@code Types.SMALLINT}
   */
  @Override
  public int getSqlType() {
    return Types.SMALLINT;
  }
}
