package work.myfavs.framework.example.config.serializer.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import work.myfavs.framework.orm.util.lang.NString;

public class NStringModule extends SimpleModule {

  public NStringModule() {
    addSerializer(NString.class, new NStringJsonSerializer());
  }
}
