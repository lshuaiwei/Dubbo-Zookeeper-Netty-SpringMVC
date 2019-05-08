package dubbo.consumer.controller;

import dubbo.common.pojo.User;
import dubbo.common.service.HelloService;
import dubbo.common.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lenovo on 2015/5/11.
 */
@Controller
public class TestController {

  @Autowired
  private HelloService helloService;

  @Autowired
  private UserService userService;

  @ResponseBody
  @RequestMapping("/test/{param}")
  public String index(@PathVariable("param") String param) {
    return helloService.sayHello(param);
  }


  @RequestMapping("/user/all")
  @ResponseBody
  public List<User> userAll() {
    return userService.getAll();
  }
}
