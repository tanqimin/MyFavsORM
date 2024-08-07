package work.myfavs.framework.orm.entity.test;

import work.myfavs.framework.orm.entity.SnowflakeExample;
import work.myfavs.framework.orm.entity.enums.TypeEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface ISnowflakeTest {
  List<SnowflakeExample> SNOW_FLAKES = new ArrayList<>();

  default void initSnowflakes() {
    SNOW_FLAKES.clear();

    SnowflakeExample obj1 = new SnowflakeExample();
    obj1.setCreated(new Date());
    obj1.setName("S1");
    obj1.setPrice(new BigDecimal("199.00"));
    obj1.setType(TypeEnum.FOOD);

    SnowflakeExample obj2 = new SnowflakeExample();
    obj2.setCreated(new Date());
    obj2.setName("S2");
    obj2.setPrice(new BigDecimal("299.00"));
    obj2.setType(TypeEnum.DRINK);

    SnowflakeExample obj3 = new SnowflakeExample();
    obj3.setCreated(new Date());
    obj3.setName("S3");
    obj3.setPrice(new BigDecimal("399.00"));
    obj3.setType(TypeEnum.FOOD);

    SNOW_FLAKES.add(obj1);
    SNOW_FLAKES.add(obj2);
    SNOW_FLAKES.add(obj3);
  }
}
