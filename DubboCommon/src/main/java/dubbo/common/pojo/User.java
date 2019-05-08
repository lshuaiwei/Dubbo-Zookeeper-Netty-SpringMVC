package dubbo.common.pojo;


import lombok.Data;

@Data
public class User implements java.io.Serializable {

  private int id;
  private String name;
  private int age;
  private String email;
}
