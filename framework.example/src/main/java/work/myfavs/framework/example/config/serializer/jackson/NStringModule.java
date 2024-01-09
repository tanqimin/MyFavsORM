package work.myfavs.framework.example.config.serializer.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import work.myfavs.framework.orm.util.lang.NVarchar;

public class NStringModule extends SimpleModule {

  public NStringModule() {
    addSerializer(NVarchar.class, new NStringJsonSerializer());
  }
}
