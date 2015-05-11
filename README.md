# Dubbo-Zookeeper-Netty-SpringMVC

1. dubbo-common 提供公共接口；
2. dubbo-provide是服务提供者（接口的实现）；
3. dubbo-consumer是服务器消费者（接口的调用）；
4. zookeeper管理注册中心（负载均衡，资源同步-当然前提是集群）；
5. springmvc写restful接口；
6. netty提供服务器，客户端的请求由spring的DispatcherServlet处理；
