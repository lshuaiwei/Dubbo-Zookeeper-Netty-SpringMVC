package dubbo.provider.service.impl;

import dubbo.common.service.HelloService;
import org.springframework.stereotype.Service;

/**
 * Created by lenovo on 2015/5/11.
 */
@Service
public class HelloServiceImpl implements HelloService {

  public String sayHello(String str) {
    return "Hello " + str;
  }
}
