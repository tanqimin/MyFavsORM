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
 * {@code Float} 类型的属性处理器.
 * <p>用于处理 Java {@link Float} 类型与数据库 {@code FLOAT} 类型之间的相互转换.</p>
 */
public class FloatPropertyHandler extends NumberPropertyHandler<Float> {

  /**
   * 构造 FloatPropertyHandler.
   */
  public FloatPropertyHandler() {
  }

  /**
   * 构造 FloatPropertyHandler, 指定是否为原始类型.
   *
   * @param isPrimitive 是否为原始类型 {@code float}
   */
  public FloatPropertyHandler(boolean isPrimitive) {
    super(isPrimitive);
  }

  @Override
  protected Float convertNumber(Number val) {
    return val.floatValue();
  }

  @Override
  protected Float convertString(String val) {
    return Float.parseFloat(val);
  }

  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, Float param) throws SQLException {
    ps.setFloat(paramIndex, param);
  }

  /**
   * 获取 SQL 类型.
   *
   * @return {@link Types#FLOAT}
   */
  @Override
  public int getSqlType() {
    return Types.FLOAT;
  }
}
