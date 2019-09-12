package work.myfavs.framework.example.business;

import java.math.BigDecimal;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.Snowfake;
import work.myfavs.framework.example.domain.entity.Uuid;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.example.repository.repo.SnowfakeRepository;
import work.myfavs.framework.example.repository.repo.UuidRepository;

@Service
public class TestPKService {

  private final UuidRepository uuidRepository;
  private final SnowfakeRepository snowfakeRepository;

  @Autowired
  public TestPKService(UuidRepository uuidRepository, SnowfakeRepository snowfakeRepository) {

    this.uuidRepository = uuidRepository;
    this.snowfakeRepository = snowfakeRepository;
  }


  @Transactional(rollbackFor = Exception.class)
  public void testTransaction()
      throws Exception {

    Uuid uuid = new Uuid();
    uuid.setCreated(new Date());
    uuid.setName("UUID");
    uuid.setDisable(false);
    uuid.setPrice(new BigDecimal(199));
    uuid.setType(TypeEnum.DRINK);

    Snowfake snowfake = new Snowfake();
    snowfake.setCreated(new Date());
    snowfake.setName("UUID");
    snowfake.setDisable(false);
    snowfake.setPrice(new BigDecimal(199));
    snowfake.setType(TypeEnum.DRINK);
    snowfake.setConfig("");
    uuidRepository.create(uuid);
//    throwException();
    snowfakeRepository.create(snowfake);
  }

  public void throwException()
      throws Exception {
    throw new Exception();
  }
}
