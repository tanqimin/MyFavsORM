package work.myfavs.framework.example.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import work.myfavs.framework.example.business.TestPKService;
import work.myfavs.framework.example.domain.entity.Identity;
import work.myfavs.framework.example.domain.entity.Snowflake;
import work.myfavs.framework.example.repository.query.SnowfakeQuery;
import work.myfavs.framework.orm.meta.Record;
import work.myfavs.framework.orm.meta.pagination.Page;

@RequestMapping("/test-pk")
@RestController
public class TestPKController {

  @Autowired private SnowfakeQuery snowfakeQuery;
  @Autowired private TestPKService testPKService;

  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public ResponseEntity<Snowflake> get(HttpServletRequest request) {
    return new ResponseEntity<>(snowfakeQuery.getFirst(), HttpStatus.OK);
  }

  @RequestMapping(value = "/find-map", method = RequestMethod.GET)
  public ResponseEntity<Map<String, Identity>> findMap() {
    return new ResponseEntity<>(testPKService.findMap(), HttpStatus.OK);
  }

  @RequestMapping(value = "/find-identity-list-by-cond", method = RequestMethod.GET)
  public ResponseEntity<List<Record>> findIdentityListByCond() {
    return new ResponseEntity(testPKService.findIdentityListByCond(), HttpStatus.OK);
  }

  @RequestMapping(value = "/create-identity", method = RequestMethod.GET)
  public ResponseEntity createIdentity() {
    testPKService.createIdentity();
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/update-identity/{id}", method = RequestMethod.GET)
  public ResponseEntity updateIdentity(@PathVariable Long id) {
    testPKService.updateIdentity(id);
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/create", method = RequestMethod.GET)
  public ResponseEntity create() throws Exception {

    testPKService.testTransaction();
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.GET)
  public ResponseEntity<Integer> update() {
    return new ResponseEntity<>(testPKService.testBatchUpdate(), HttpStatus.OK);
  }

  @RequestMapping(value = "/list-identity", method = RequestMethod.GET)
  public ResponseEntity<List<Identity>> listIdentity() {
    return new ResponseEntity<>(testPKService.listIdentity(), HttpStatus.OK);
  }

  @RequestMapping(value = "/find-by-page")
  public ResponseEntity<Page<Snowflake>> findByPage() {

    return new ResponseEntity<>(snowfakeQuery.findPage(), HttpStatus.OK);
  }
}
