package work.myfavs.framework.example.config.serializer.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
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

    NVarchar nvarchar = (NVarchar) object;
    if (nvarchar == null) {
      writer.writeNull(SerializerFeature.WriteNullStringAsEmpty);
      return;
    }

    writer.writeString(nvarchar.toString());
  }
}
