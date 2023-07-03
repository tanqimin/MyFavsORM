package work.myfavs.framework.orm.entity.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import work.myfavs.framework.orm.entity.Uuid;
import work.myfavs.framework.orm.entity.enums.TypeEnum;

public interface IUuidTest {
  List<Uuid> UUIDS = new ArrayList<>();

  default void initUuids() {
    UUIDS.clear();

    Uuid obj1 = new Uuid();
    obj1.setCreated(new Date());
    obj1.setName("S1");
    obj1.setPrice(new BigDecimal("199.00"));
    obj1.setType(TypeEnum.FOOD);

    Uuid obj2 = new Uuid();
    obj2.setCreated(new Date());
    obj2.setName("S2");
    obj2.setPrice(new BigDecimal("299.00"));
    obj2.setType(TypeEnum.DRINK);

    Uuid obj3 = new Uuid();
    obj3.setCreated(new Date());
    obj3.setName("S3");
    obj3.setPrice(new BigDecimal("399.00"));
    obj3.setType(TypeEnum.FOOD);

    UUIDS.add(obj1);
    UUIDS.add(obj2);
    UUIDS.add(obj3);
  }
}
