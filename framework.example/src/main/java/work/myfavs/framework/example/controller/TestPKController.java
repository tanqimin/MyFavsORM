package work.myfavs.framework.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import work.myfavs.framework.example.business.TestPKService;
import work.myfavs.framework.example.domain.entity.Snowfake;
import work.myfavs.framework.example.repository.query.SnowFakeQuery;

@RequestMapping("/test-pk")
@RestController
public class TestPKController {

  @Autowired
  private SnowFakeQuery snowFakeQuery;
  @Autowired
  private TestPKService testPKService;

  @RequestMapping(value = "/get", method = RequestMethod.GET)
  public ResponseEntity<Snowfake> get() {

    return new ResponseEntity<>(snowFakeQuery.get(), HttpStatus.OK);
  }

  @RequestMapping(value = "/create", method = RequestMethod.GET)
  public ResponseEntity create()
      throws Exception {
    testPKService.testTransaction();
    return new ResponseEntity(HttpStatus.OK);
  }
}
