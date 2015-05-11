package dubbo.consumer.test;

import dubbo.common.service.HelloService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by lenovo on 2015/5/11.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:dubbo-consumer.xml")
public class ConsumerTest {
    @Autowired
    private HelloService helloService ;

    @Test
    public void test(){
        System.out.println(helloService.sayHello("test"))  ;
    }

}
