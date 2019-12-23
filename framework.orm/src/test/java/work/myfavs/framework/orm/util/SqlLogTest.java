package work.myfavs.framework.orm.util;

import cn.hutool.core.date.StopWatch;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class SqlLogTest {

  @Test
  public void showResult() {

    Person p1 = new Person("karl", 18);
    Person p2 = new Person("tam", 22);
    Person p3 = new Man("tam", 22, "worker");

    List<Person> personList = new ArrayList<>();
    for (int i = 0;
         i < 1000000;
         i++) {
      personList.add(new Person("name" + i, 18));
    }

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    StringBuilder sb = new StringBuilder();
    for (Person person : personList) {
      sb.append(JSONUtil.parseObj(person).toString());
      sb.append("\n");
    }
    stopWatch.stop();
    log.debug(sb.toString());
    log.debug("use : {}", stopWatch.getLastTaskTimeMillis());
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class Person {

    private String name;
    private int    age;

  }

  @Data
  class Man
      extends Person {

    public Man(String name, int age, String job) {

      super(name, age);
      this.job = job;
    }

    private String job;

  }

}