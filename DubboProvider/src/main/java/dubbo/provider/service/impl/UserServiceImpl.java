package dubbo.provider.service.impl;

import dubbo.common.pojo.User;
import dubbo.common.service.UserService;
import dubbo.provider.mapper.UserMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  UserMapper userMapper;

  public List<User> getAll() {
    return userMapper.selectAll();
  }
}
