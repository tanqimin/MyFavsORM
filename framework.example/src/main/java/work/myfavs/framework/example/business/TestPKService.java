package work.myfavs.framework.example.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.myfavs.framework.example.domain.entity.Identity;
import work.myfavs.framework.example.domain.entity.Snowflake;
import work.myfavs.framework.example.domain.entity.Uuid;
import work.myfavs.framework.example.domain.enums.TypeEnum;
import work.myfavs.framework.example.repository.repo.IdentityRepository;
import work.myfavs.framework.example.repository.repo.SnowflakeRepository;
import work.myfavs.framework.example.repository.repo.UuidRepository;
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

    Uuid uuid = new Uuid();
    uuid.setCreated(new Date());
    uuid.setName("UUID");
    uuid.setDisable(false);
    uuid.setPrice(new BigDecimal(199));
    uuid.setType(TypeEnum.DRINK);

    Snowflake snowflake = new Snowflake();
    snowflake.setCreated(new Date());
    snowflake.setName("UUID");
    snowflake.setDisable(false);
    snowflake.setPrice(new BigDecimal(199));
    snowflake.setType(TypeEnum.DRINK);
    snowflake.setConfig("");
    uuidRepository.create(uuid);
//    throwException();
    snowflakeRepository.create(snowflake);
  }

  public void throwException()
      throws Exception {

    throw new Exception();
  }

  @Transactional(rollbackFor = Exception.class)
  public void createIdentity() {
    Identity identity = new Identity();
    identity.setName("TEST");
    identity.setType(TypeEnum.DRINK);
    identity.setPrice(new BigDecimal(199.00));
    identity.setDisable(false);

    identityRepository.create(identity);
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
    return identityRepository.func().find(new Sql("SELECT * FROM tb_identity")).collect(Collectors.toList());
  }
}
