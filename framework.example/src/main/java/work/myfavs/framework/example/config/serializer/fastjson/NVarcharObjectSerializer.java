package work.myfavs.framework.example.config.serializer.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.io.IOException;
import java.lang.reflect.Type;

public class NVarcharObjectSerializer implements ObjectSerializer {
  @Override
  public void write(JSONSerializer serializer, //
                    Object object, //
                    Object fieldName, //
                    Type fieldType, //
                    int features) throws IOException {
    SerializeWriter writer = serializer.out;

    if (object instanceof NVarchar) {
      NVarchar nvarchar = (NVarchar) object;
      writer.writeString(nvarchar.toString());
    }

  }
}
