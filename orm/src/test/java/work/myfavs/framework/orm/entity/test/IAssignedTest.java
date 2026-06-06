package work.myfavs.framework.orm.entity.test;

import work.myfavs.framework.orm.entity.AssignedExample;

import java.util.ArrayList;
import java.util.List;

public interface IAssignedTest {
  List<AssignedExample> ASSIGNEDS = new ArrayList<>();

  default void initAssigned() {
    ASSIGNEDS.clear();

    AssignedExample obj1 = new AssignedExample("A000B000C000D000E000F001");
    AssignedExample obj2 = new AssignedExample("A000B000C000D000E000F002");
    AssignedExample obj3 = new AssignedExample("A000B000C000D000E000F003");

    ASSIGNEDS.add(obj1);
    ASSIGNEDS.add(obj2);
    ASSIGNEDS.add(obj3);
  }
}
