package work.myfavs.framework.orm.entity.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import work.myfavs.framework.orm.entity.Identity;
import work.myfavs.framework.orm.entity.enums.TypeEnum;

public interface IIdentityTest {
  List<Identity> IDENTITIES = new ArrayList<>();

  default void initIdentities() {
    IDENTITIES.clear();

    Identity obj1 = new Identity();
    obj1.setCreated(new Date());
    obj1.setName("S1");
    obj1.setPrice(new BigDecimal("199.00"));
    obj1.setType(TypeEnum.FOOD);

    Identity obj2 = new Identity();
    obj2.setCreated(new Date());
    obj2.setName("S2");
    obj2.setPrice(new BigDecimal("299.00"));
    obj2.setType(TypeEnum.DRINK);

    Identity obj3 = new Identity();
    obj3.setCreated(new Date());
    obj3.setName("S3");
    obj3.setPrice(new BigDecimal("399.00"));
    obj3.setType(TypeEnum.FOOD);

    IDENTITIES.add(obj1);
    IDENTITIES.add(obj2);
    IDENTITIES.add(obj3);
  }
}
