package work.myfavs.framework.sb2.demo.config.serializer.fastjson;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.lang.reflect.Type;

/**
 * Fastjson {@link NVarchar} 反序列化器.
 */
public class NVarcharObjectDeserializer implements ObjectDeserializer {
  /**
   * 反序列化为 {@code NVarchar} 对象.
   *
   * @param parser    JSON 解析器
   * @param type      目标类型
   * @param fieldName 字段名称
   * @param <T>       返回值类型
   * @return 反序列化后的 {@code NVarchar} 对象
   */
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

  /**
   * 获取快速匹配的 JSON token 类型.
   *
   * @return {@link JSONToken#LITERAL_STRING}
   */
  @Override
  public int getFastMatchToken() {
    return JSONToken.LITERAL_STRING;
  }
}
