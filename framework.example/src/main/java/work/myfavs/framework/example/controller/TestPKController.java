package work.myfavs.framework.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import work.myfavs.framework.example.business.TestPKService;
import work.myfavs.framework.example.domain.entity.TestSnowFake;
import work.myfavs.framework.example.domain.entity.TestUUID;
import work.myfavs.framework.example.repository.query.IdentityQuery;
import work.myfavs.framework.example.repository.query.SnowFakeQuery;
import work.myfavs.framework.example.repository.query.UUIDQuery;
import work.myfavs.framework.orm.meta.pagination.Page;
import work.myfavs.framework.orm.meta.pagination.PageLite;

@RequestMapping("/test-pk")
@RestController
public class TestPKController {

  @Autowired
  private UUIDQuery     uuidQuery;
  @Autowired
  private IdentityQuery identityQuery;
  @Autowired
  private SnowFakeQuery snowFakeQuery;
  @Autowired
  private TestPKService testPKService;

  @RequestMapping(value = "/uuid/create", method = RequestMethod.GET)
  public ResponseEntity<Integer> uuid() {

    return new ResponseEntity<>(testPKService.createUUID(), HttpStatus.OK);
  }

  @RequestMapping(value = "/identity/create", method = RequestMethod.GET)
  public ResponseEntity<Integer> identity() {

    return new ResponseEntity<>(testPKService.createIdentity(), HttpStatus.OK);
  }

  @RequestMapping(value = "/snow-fake/create", method = RequestMethod.GET)
  public ResponseEntity<Integer> snowFake() {

    return new ResponseEntity<>(testPKService.createSnowFake(), HttpStatus.OK);
  }

  @RequestMapping(value = "/snow-fake/list-page-lite-{currentPage}-{pageSize}")
  public ResponseEntity<PageLite<TestSnowFake>> snowFakePageLite(@PathVariable("currentPage") long currentPage,
                                                                 @PathVariable("pageSize") long pageSize) {

    return new ResponseEntity<>(snowFakeQuery.findPageLite(currentPage, pageSize), HttpStatus.OK);
  }

  @RequestMapping(value = "/uuid/list-page-{currentPage}-{pageSize}")
  public ResponseEntity<Page<TestUUID>> uuidPage(@PathVariable("currentPage") long currentPage, @PathVariable("pageSize") long pageSize) {

    return new ResponseEntity<>(uuidQuery.findPage(currentPage, pageSize), HttpStatus.OK);
  }

  @RequestMapping(value = "/uuid/list-page-lite-{currentPage}-{pageSize}")
  public ResponseEntity<PageLite<TestUUID>> uuidPageLite(@PathVariable("currentPage") long currentPage,
                                                         @PathVariable("pageSize") long pageSize) {

    return new ResponseEntity<>(uuidQuery.findPageLite(currentPage, pageSize), HttpStatus.OK);
  }


  @RequestMapping(value = "/uuid/update-created", method = RequestMethod.GET)
  public ResponseEntity<Integer> updateCreated() {

    return new ResponseEntity<>(testPKService.updateCreated(), HttpStatus.OK);
  }

  @RequestMapping(value = "/uuid/delete-foods", method = RequestMethod.GET)
  public ResponseEntity<Integer> deleteFoods() {

    return new ResponseEntity<>(testPKService.deleteFoods(), HttpStatus.OK);
  }

}
