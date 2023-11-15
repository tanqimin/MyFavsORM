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

public class ShortPropertyHandler extends NumberPropertyHandler<Short> {

  public ShortPropertyHandler() {
  }

  public ShortPropertyHandler(boolean isPrimitive) {
    super(isPrimitive);
  }

  @Override
  protected Short nullPrimitiveValue() {
    return 0;
  }

  @Override
  protected Short convert(Object val) {
    return Convert.toShort(val);
  }

  @Override
  protected void setParameter(PreparedStatement ps, int paramIndex, Short param) throws SQLException {
    ps.setShort(paramIndex, param);
  }

  @Override
  public int getSqlType() {
    return Types.SMALLINT;
  }
}
