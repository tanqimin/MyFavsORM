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

import cn.hutool.core.convert.Convert;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class DoublePropertyHandler extends NumberPropertyHandler<Double> {

  public DoublePropertyHandler() {
  }

  public DoublePropertyHandler(boolean isPrimitive) {
    super(isPrimitive);
  }

  @Override
  protected Double nullPrimitiveValue() {
    return 0.0d;
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, Double param) throws SQLException {

    if (param == null) {
      ps.setNull(paramIndex, getSqlType());
      return;
    }
    ps.setDouble(paramIndex, param);
  }

  @Override
  protected Double convert(Object val) {
    return Convert.toDouble(val);
  }

  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, Double param) throws SQLException {
    ps.setDouble(paramIndex, param);
  }


  @Override
  public int getSqlType() {
    return Types.DOUBLE;
  }
}
