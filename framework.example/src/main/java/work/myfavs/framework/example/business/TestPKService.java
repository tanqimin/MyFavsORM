package work.myfavs.framework.example.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.Identity;
import work.myfavs.framework.example.domain.entity.Uuid;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.example.repository.repo.IdentityRepository;
import work.myfavs.framework.example.repository.repo.SnowflakeRepository;
import work.myfavs.framework.example.repository.repo.UuidRepository;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.clause.Cond;
import work.myfavs.framework.orm.meta.clause.Sql;

@Service
public class TestPKService {

  private final UuidRepository uuidRepository;
  private final SnowflakeRepository snowflakeRepository;
  private final IdentityRepository identityRepository;

  @Autowired
  public TestPKService(UuidRepository uuidRepository,
      SnowflakeRepository snowflakeRepository,
      IdentityRepository identityRepository) {

    this.uuidRepository = uuidRepository;
    this.snowflakeRepository = snowflakeRepository;
    this.identityRepository = identityRepository;
  }


  @Transactional(rollbackFor = Exception.class)
  public void testTransaction()
      throws Exception {

    List<Uuid> uuidList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Uuid uuid = new Uuid();
      uuid.setCreated(new Date());
      uuid.setName("UUID");
      uuid.setDisable(false);
      uuid.setPrice(new BigDecimal(199));
      uuid.setType(TypeEnum.DRINK);
      uuidList.add(uuid);
    }

    uuidRepository.create(uuidList);
  }

  public void throwException()
      throws Exception {

    throw new Exception();
  }

  @Transactional(rollbackFor = Exception.class)
  public void createIdentity() {
    if (identityRepository.existsByCond(Cond.eq("name", "TEST"))) {
      return;
    }

    List<Identity> entities = new ArrayList<>();
    Identity i1 = new Identity();
    i1.setName("TEST");
    i1.setType(TypeEnum.DRINK);
    i1.setPrice(new BigDecimal(199.00));
    i1.setDisable(false);

    Identity i2 = new Identity();
    i2.setName("TEST");
    i2.setType(TypeEnum.FOOD);
    i2.setPrice(new BigDecimal(199.00));
    i2.setDisable(false);

    entities.add(i1);
    entities.add(i2);

    identityRepository.create(entities);
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateIdentity(Long id) {
    Identity identity = identityRepository.getById(id);
    if (identity != null) {
      identity.setType(TypeEnum.FOOD);
      identity.setName("UPD_TEST");
      identityRepository.update(identity);
    }
  }

  @Transactional(readOnly = true)
  public List<Identity> listIdentity() {
    return identityRepository
        .find(new Sql("SELECT * FROM tb_identity"));
  }

  public List<Record> findIdentityListByCond() {
    return null;
  }

  @Transactional()
  public int testBatchUpdate() {
    final List<Identity> identities = listIdentity();
    for (Identity identity : identities) {
      identity.setName(UUID.randomUUID().toString());
      identity.setPrice(new BigDecimal(Math.random()));
    }
    return identityRepository.update(identities, new String[]{"name", "price"});
  }
}
