package work.myfavs.framework.example.config.serializer.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.lang.reflect.Type;

public class NVarcharObjectDeserializer implements ObjectDeserializer {
  @SuppressWarnings("unchecked")
  @Override
  public <T> T deserialze(
      DefaultJSONParser parser,
      Type type,
      Object fieldName) {
    final JSONLexer lexer = parser.getLexer();
    if (lexer.token() == JSONToken.LITERAL_STRING) {
      String val = lexer.stringVal();
      lexer.nextToken(JSONToken.COMMA);
      return (T) new NVarchar(val);
    }

    if (lexer.token() == JSONToken.LITERAL_INT) {
      String val = lexer.numberString();
      lexer.nextToken(JSONToken.COMMA);
      return (T) new NVarchar(val);
    }

    Object value = parser.parse();

    if (value == null) {
      return null;
    }

    return (T) new NVarchar(value.toString());
  }

  @Override
  public int getFastMatchToken() {
    return JSONToken.LITERAL_STRING;
  }
}
