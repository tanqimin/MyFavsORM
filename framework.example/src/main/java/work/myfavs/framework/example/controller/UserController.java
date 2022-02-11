package work.myfavs.framework.example.controller;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import work.myfavs.framework.example.business.UserService;
import work.myfavs.framework.example.domain.entity.User;
import work.myfavs.framework.orm.meta.pagination.Page;

@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public ResponseEntity<Long> saveUser(RequestEntity<User> entity) {
    User user = userService.saveUser(entity.getBody());
    return ResponseEntity.ok().body(user.getId());
  }

  @RequestMapping(value = "/find-by-page")
  public ResponseEntity<Page<User>> findByPage(){
    return ResponseEntity.ok().body(userService.findByPage());
  }
}
