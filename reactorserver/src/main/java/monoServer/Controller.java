package monoServer;

import monoServer.actorImpl.SayHelloFristActor;
import monoServer.actorImpl.SyncDBActor;
import monoServer.actors.topo.ActorTopo;
import monoServer.enums.ActorGroupIdEnum;
import monoServer.redission.RedissionClient;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class Controller {

    @GetMapping("/index")
    public String index() {
        try {
            long delay = 1+Double.valueOf(Math.random() * 50).longValue();
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "index";
    }

    @RequestMapping(value = "/requestGet")
    public String requestGet() {

        return "requestGet";
    }

    @Autowired
    private SayHelloService sayHelloService;

    public static  AtomicLong count = new AtomicLong();
    public static  long lastTime;

    public static  AtomicLong acceptCount = new AtomicLong();

    @Autowired
    public  MyRoundLoadBalancer myRoundLoadBalancer;

    @RequestMapping(value = "/sayHello")
    public Mono<String> sayHello() throws InstantiationException, IllegalAccessException {
      // System.out.println("Request received");
        //Object userDao111 = SpringContext.getBean("userDao111");
        Map<String,Object> requestData = new HashMap<>();
        requestData.put("userId","16999999");
        Mono<String> kk = Mono.just("hello");
            return  ActorTopo.newBuilder(ActorGroupIdEnum.SAY_HELLO)
                    .frist(SayHelloFristActor.class)
                    .topo(SyncDBActor.class)
                    .build(20)
                    .start(requestData);
//        //myRoundLoadBalancer.chose("feign-server");
//        Mono<String> monoResult = Mono.create(deferredResult->{
//            String hhhh =userId;
//            String kvalue = kk.block();
//            sayHelloService.sayHello(deferredResult);
//        });
//
//
//      // System.out.println("Servlet thread released");
//
//        if(acceptCount.addAndGet(1)%10000==0){
//            System.out.println("acceptCount:"+acceptCount);
//        }
//
//        return monoResult;
    }


    @RequestMapping(value = "/getRedis")
    public String getRedis(String key) {
        RedissonClient redisson= RedissionClient.getInstance();
        RBucket<String> bucket =redisson.getBucket(key);
        System.out.println("bucket:"+bucket.get());
        // fixme 为何这里一直取出来的是null


//        RBuckets buckets = redisson.getBuckets();
//        Map<String, String> loadedBuckets = buckets.get("myBucket1", "myBucket2", "myBucket3");
//        System.out.println("myBucket1:"+loadedBuckets.get("myBucket1"));
//        System.out.println("myBucket2:"+loadedBuckets.get("myBucket2"));
//        System.out.println("myBucket3:"+loadedBuckets.get("myBucket3"));


        return "getRedis";
    }
    @RequestMapping(value = "/setRedis")
    public String setRedis(String key,String value) {

        RBucket<String> bucket = RedissionClient.getInstance().getBucket(key);
        bucket.set(value);
        return "setRedis";
    }
}
