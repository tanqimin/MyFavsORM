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
 * {@code Double} 类型的属性处理器.
 * <p>用于处理 Java {@link Double} 类型与数据库 {@code DOUBLE} 类型之间的相互转换.</p>
 */
public class DoublePropertyHandler extends NumberPropertyHandler<Double> {

  /**
   * 构造 DoublePropertyHandler.
   */
  public DoublePropertyHandler() {
  }

  /**
   * 构造 DoublePropertyHandler, 指定是否为原始类型.
   *
   * @param isPrimitive 是否为原始类型 {@code double}
   */
  public DoublePropertyHandler(boolean isPrimitive) {
    super(isPrimitive);
  }

  @Override
  protected Double convertNumber(Number val) {
    return val.doubleValue();
  }

  @Override
  protected Double convertString(String val) {
    return Double.parseDouble(val);
  }

  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, Double param) throws SQLException {
    ps.setDouble(paramIndex, param);
  }


  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#DOUBLE}
   */
  @Override
  public int getSqlType() {
    return Types.DOUBLE;
  }
}
