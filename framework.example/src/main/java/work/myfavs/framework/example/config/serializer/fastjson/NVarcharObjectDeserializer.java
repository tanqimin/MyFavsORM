package work.myfavs.framework.example.config.serializer.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.lang.reflect.Type;

public class NVarcharObjectDeserializer implements ObjectDeserializer {
  @SuppressWarnings("unchecked")
  @Override
  public NVarchar deserialze(
      DefaultJSONParser parser,
      Type type,
      Object fieldName) {
    String val = parser.getLexer().stringVal();
    return new NVarchar(val);
  }

  @Override
  public int getFastMatchToken() {
    return JSONToken.LITERAL_STRING;
  }
}
