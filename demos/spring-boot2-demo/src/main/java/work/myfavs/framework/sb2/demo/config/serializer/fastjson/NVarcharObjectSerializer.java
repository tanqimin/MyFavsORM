package work.myfavs.framework.sb2.demo.config.serializer.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import work.myfavs.framework.orm.util.lang.NVarchar;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Fastjson {@link NVarchar} 序列化器.
 */
public class NVarcharObjectSerializer implements ObjectSerializer {
  /**
   * 将 {@code NVarchar} 对象序列化为 JSON 字符串.
   *
   * @param serializer JSON 序列化器
   * @param object     待序列化的对象
   * @param fieldName  字段名称
   * @param fieldType  字段类型
   * @param features   序列化特性
   * @throws IOException 序列化异常
   */
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
