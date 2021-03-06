package monoServer;

import monoServer.actorImpl.*;
import monoServer.actors.topo.ActorTopo;
import monoServer.enums.ActorGroupIdEnum;
import monoServer.feignclient.FeignClientSao;
import monoServer.redission.RedissionClient;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/requestGet",method = RequestMethod.GET)
    public String requestGet() {
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "requestGet";
    }

    @Autowired
    private SayHelloService sayHelloService;

    public static  AtomicLong count = new AtomicLong();
    public static  long lastTime;

    public static  AtomicLong acceptCount = new AtomicLong();

    @Autowired
    public  MyRoundLoadBalancer myRoundLoadBalancer;

    @Autowired
    public FeignClientSao feignClientSao;

    @RequestMapping(value = "/sayHello",method = RequestMethod.GET)
    public Mono<String> sayHello(){
    //    feignClientSao.hi2("uft-8",111L,"name","body","aaa");
        Map<String,Object> requestData = new HashMap<>();
        requestData.put("userId","16999999");
        Mono<String> kk = Mono.just("hello");
            return  ActorTopo.newBuilder(ActorGroupIdEnum.SAY_HELLO)
                    .frist(SayHelloFristActor.class)
                    //.topo(SyncDBActor.class, SayHelloHttpGetActor.class, SayHelloHttpPutActor.class, SayHelloHttpPostActor.class,SayHelloHttpDeleteActor.class)
                    .topo(LettcueRedisActor.class)
                    .parall(20)
                    .build()
                    .start(requestData);
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
