package work.myfavs.framework.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import work.myfavs.framework.example.business.TestPKService;
import work.myfavs.framework.example.domain.entity.Snowflake;
import work.myfavs.framework.example.repository.query.SnowfakeQuery;
import work.myfavs.framework.orm.meta.pagination.Page;

@RequestMapping("/test-pk")
@RestController
public class TestPKController {

  @Autowired
  private SnowfakeQuery snowfakeQuery;
  @Autowired
  private TestPKService testPKService;

  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public ResponseEntity<Snowflake> get() {

    return new ResponseEntity<>(snowfakeQuery.getFirst(), HttpStatus.OK);
  }

  @RequestMapping(value = "/create-identity", method = RequestMethod.GET)
  public ResponseEntity createIdentity(){
    testPKService.createIdentity();
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/update-identity/{id}", method = RequestMethod.GET)
  public ResponseEntity updateIdentity(@PathVariable Long id){
    testPKService.updateIdentity(id);
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/create", method = RequestMethod.GET)
  public ResponseEntity create()
      throws Exception {

    testPKService.testTransaction();
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/find-by-page")
  public ResponseEntity<Page<Snowflake>> findByPage() {

    return new ResponseEntity<>(snowfakeQuery.findPage(), HttpStatus.OK);
  }

}
