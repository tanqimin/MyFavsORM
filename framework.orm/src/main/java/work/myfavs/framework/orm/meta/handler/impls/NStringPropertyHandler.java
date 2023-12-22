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
import work.myfavs.framework.orm.util.lang.NString;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class NStringPropertyHandler extends PropertyHandler<NString> {


  public NStringPropertyHandler() {

  }

  @Override
  public NString convert(ResultSet rs, int columnIndex, Class<NString> clazz) throws SQLException {

    String val = rs.getNString(columnIndex);
    if (Objects.isNull(val)) {
      return null;
    }

    return new NString(val);
  }

  @Override
  public void addParameter(PreparedStatement ps, int paramIndex, NString param) throws SQLException {

    ps.setNString(paramIndex, param.toString());
  }

  @Override
  public int getSqlType() {

    return Types.NVARCHAR;
  }
}
