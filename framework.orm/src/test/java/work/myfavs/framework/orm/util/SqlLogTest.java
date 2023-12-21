package work.myfavs.framework.orm.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SqlLogTest {

  private static final Logger log = LoggerFactory.getLogger(SqlLogTest.class);

  @Test
  public void showResult() {

    Person p1 = new Person("karl", 18);
    Person p2 = new Person("tam", 22);
    Person p3 = new Man("tam", 22, "worker");

    List<Person> personList = new ArrayList<>();
    for (int i = 0; i < 1000000; i++) {
      personList.add(new Person("name" + i, 18));
    }

    long      start     = System.currentTimeMillis();
    StringBuilder sb = new StringBuilder();
    for (Person person : personList) {
      //      sb.append(JSON.parseObj(person)
      //                        .toString());
      sb.append("\n");
    }
    long stop = System.currentTimeMillis();
    log.debug(sb.toString());
    log.debug("use : {}", stop - start);
  }

  class Person {

    private String name;
    private int    age;

    public String getName() {

      return name;
    }

    public void setName(String name) {

      this.name = name;
    }

    public int getAge() {

      return age;
    }

    public void setAge(int age) {

      this.age = age;
    }

    public Person(String name, int age) {

      this.name = name;
      this.age = age;
    }
  }

  class Man extends Person {

    public Man(String name, int age, String job) {

      super(name, age);
      this.job = job;
    }

    private String job;

    public String getJob() {

      return job;
    }

    public void setJob(String job) {

      this.job = job;
    }
  }
}
