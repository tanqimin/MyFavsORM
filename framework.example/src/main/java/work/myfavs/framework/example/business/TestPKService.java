package work.myfavs.framework.example.business;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.TestIdentity;
import work.myfavs.framework.example.domain.entity.TestSnowFake;
import work.myfavs.framework.example.domain.entity.TestUUID;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.example.repository.IdentityRepository;
import work.myfavs.framework.example.repository.SnowFakeRepository;
import work.myfavs.framework.example.repository.UUIDRepository;

@Service
public class TestPKService {

  @Autowired
  private UUIDRepository     uuidRepository;
  @Autowired
  private IdentityRepository identityRepository;
  @Autowired
  private SnowFakeRepository snowFakeRepository;

  private final static int BITCH_SIZE = 10000 * 1;

  @Transactional
  public int createUUID() {

    List<TestUUID> list = new ArrayList<>();
    for (int i = 0;
         i < BITCH_SIZE;
         i++) {
      TestUUID entity = new TestUUID();
      entity.setName("NAME_" + i);
      entity.setCreated(LocalDateTime.now());
      entity.setDisable(i % 2 == 0);
      entity.setPrice(new BigDecimal(199.99));
      entity.setType(i % 2 == 0
                         ? TypeEnum.FOOD
                         : TypeEnum.DRINK);
      list.add(entity);
    }
    return uuidRepository.create(list);
  }

  @Transactional
  public int createIdentity() {

    List<TestIdentity> list = new ArrayList<>();
    for (int i = 0;
         i < BITCH_SIZE;
         i++) {
      TestIdentity entity = new TestIdentity();
      entity.setName("NAME_" + i);
      entity.setCreated(LocalDateTime.now());
      entity.setDisable(i % 2 == 0);
      entity.setPrice(new BigDecimal(199.99));
      entity.setType(i % 2 == 0
                         ? TypeEnum.FOOD
                         : TypeEnum.DRINK);
      list.add(entity);
      identityRepository.create(entity);
    }
//    return identityRepository.create(list);
    return list.size();
  }


  @Transactional
  public int createSnowFake() {

    List<TestSnowFake> list = new ArrayList<>();

    for (int i = 0;
         i < BITCH_SIZE;
         i++) {
      TestSnowFake entity = new TestSnowFake();
      entity.setName("NAME_" + i);
      entity.setCreated(LocalDateTime.now());
      entity.setDisable(i % 2 == 0);
      entity.setPrice(new BigDecimal(199.99));
      entity.setType(i % 2 == 0
                         ? TypeEnum.FOOD
                         : TypeEnum.DRINK);

//      Config config = new Config();
//      config.setUrl(StrUtil.format("http://www.baidu.com/{}", i));
//      config.setUsername(entity.getName());
//      config.setPassword("123456");
//      entity.setConfig(config);
      list.add(entity);
    }
    return snowFakeRepository.create(list);
  }

  @Transactional
  public int updateCreated() {

    List<TestUUID> drinks = uuidRepository.findAllDrink();
    drinks.forEach(d -> d.setCreated(LocalDateTime.now()));
    return uuidRepository.update(drinks, new String[]{"created"});
  }

  @Transactional
  public int deleteFoods() {

    List<String> ids = uuidRepository.findFoodsIds();
    return uuidRepository.deleteByIds(ids);
  }


}
