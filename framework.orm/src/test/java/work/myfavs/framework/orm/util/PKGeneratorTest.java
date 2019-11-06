package work.myfavs.framework.orm.util;

import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Assert;
import org.junit.Test;

public class PKGeneratorTest {

  @Test
  public void nextSnowFakeId() {

    int                loop    = 10000;
    Map<Long, Integer> counter = new HashMap<>();
    for (int i = 0;
         i < loop;
         i++) {
      final long snowFakeId = PKGenerator.nextSnowFakeId(1L, 1L);
      if (counter.containsKey(snowFakeId)) {
        counter.put(snowFakeId, counter.get(snowFakeId) + 1);
      } else {
        counter.put(snowFakeId, 1);
      }
    }

    for (Entry<Long, Integer> entry : counter.entrySet()) {
      System.out.println(StrUtil.format("{}:{}", entry.getKey(), entry.getValue()));
    }

    Assert.assertEquals(counter.size(), loop);


  }

}