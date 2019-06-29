## Architecture
![Architecture](doc/image/architecture.png)

## Old
![Old](doc/image/old.png)

## 组件选型
``
1、SpringBoot 2.x 的WebFlux直接提供异步容器
2、Lettuce客户端支持netty实现的reactor客户端
3、HttpComponets组件httpclient4.5支持异步client
4、Akka、Reactor-core异步执行框架
5、目前支持ractor的数据库，redis，mongo


6、封装topo组装细节，封装回调函数注册细节，开发者不用关注akka调度细节，开发者使用同步编程思维决定下一步要做什么。
``


## 严格超时监控
``
1、单个异步执行单元不能超过5ms
2、整个业务执行时间不能超过1000ms
``