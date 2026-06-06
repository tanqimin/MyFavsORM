package work.myfavs.framework.orm.entity.test;

import work.myfavs.framework.orm.entity.UuidExample;
import work.myfavs.framework.orm.entity.enums.TypeEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface IUuidTest {
  List<UuidExample> UUIDS = new ArrayList<>();

  default void initUuids() {
    UUIDS.clear();

    UuidExample obj1 = new UuidExample();
    obj1.setCreated(new Date());
    obj1.setName("S1");
    obj1.setPrice(new BigDecimal("199.00"));
    obj1.setType(TypeEnum.FOOD);

    UuidExample obj2 = new UuidExample();
    obj2.setCreated(new Date());
    obj2.setName("S2");
    obj2.setPrice(new BigDecimal("299.00"));
    obj2.setType(TypeEnum.DRINK);

    UuidExample obj3 = new UuidExample();
    obj3.setCreated(new Date());
    obj3.setName("S3");
    obj3.setPrice(new BigDecimal("399.00"));
    obj3.setType(TypeEnum.FOOD);

    UUIDS.add(obj1);
    UUIDS.add(obj2);
    UUIDS.add(obj3);
  }
}
