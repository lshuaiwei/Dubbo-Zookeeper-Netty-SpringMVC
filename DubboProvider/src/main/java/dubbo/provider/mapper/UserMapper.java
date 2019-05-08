package dubbo.provider.mapper;

import dubbo.common.pojo.User;
import java.util.List;

public interface UserMapper {
  List<User> selectAll();
}
