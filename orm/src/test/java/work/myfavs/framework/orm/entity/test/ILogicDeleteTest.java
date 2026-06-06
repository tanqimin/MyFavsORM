package work.myfavs.framework.orm.entity.test;

import work.myfavs.framework.orm.entity.LogicDeleteExample;
import work.myfavs.framework.orm.entity.enums.TypeEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ILogicDeleteTest {
  List<LogicDeleteExample> LOGIC_DELETES = new ArrayList<>();

  default void initLogicDeletes() {
    LOGIC_DELETES.clear();

    LogicDeleteExample obj1 = new LogicDeleteExample();
    obj1.setCreated(new Date());
    obj1.setName("S1");
    obj1.setPrice(new BigDecimal("199.00"));
    obj1.setType(TypeEnum.FOOD);

    LogicDeleteExample obj2 = new LogicDeleteExample();
    obj2.setCreated(new Date());
    obj2.setName("S2");
    obj2.setPrice(new BigDecimal("299.00"));
    obj2.setType(TypeEnum.DRINK);

    LogicDeleteExample obj3 = new LogicDeleteExample();
    obj3.setCreated(new Date());
    obj3.setName("S3");
    obj3.setPrice(new BigDecimal("399.00"));
    obj3.setType(TypeEnum.FOOD);

    LOGIC_DELETES.add(obj1);
    LOGIC_DELETES.add(obj2);
    LOGIC_DELETES.add(obj3);
  }
}
