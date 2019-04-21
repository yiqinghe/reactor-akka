package Mono;

import Mono.redission.RedissionClient;
import client.Main;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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


    @RequestMapping(value = "/sayHello")
    public Mono<String> sayHello() {
      // System.out.println("Request received");

        Mono<String> monoResult = Mono.create(deferredResult->{
            sayHelloService.sayHello(deferredResult);
        });

      // System.out.println("Servlet thread released");

        if(acceptCount.addAndGet(1)%10000==0){
            System.out.println("acceptCount:"+acceptCount);
        }

        return monoResult;
    }


    @RequestMapping(value = "/client")
    public String client() {
        Main.main(ReactiveWebApplication.argsMain);
        return "client";
    }
    @RequestMapping(value = "/getRedis")
    public String getRedis() {
        RedissonClient redisson= RedissionClient.getInstance();
        RBucket<String> bucket =redisson.getBucket("stringKey");
        System.out.println("bucket:"+bucket.get());

//        RBuckets buckets = redisson.getBuckets();
//        Map<String, String> loadedBuckets = buckets.get("myBucket1", "myBucket2", "myBucket3");
//        System.out.println("myBucket1:"+loadedBuckets.get("myBucket1"));
//        System.out.println("myBucket2:"+loadedBuckets.get("myBucket2"));
//        System.out.println("myBucket3:"+loadedBuckets.get("myBucket3"));


        return "getRedis"+bucket.get();
    }
    @RequestMapping(value = "/setRedis")
    public String setRedis() {

        RBucket<String> bucket = RedissionClient.getInstance().getBucket("stringKey");
        bucket.set("testValue");
        return "setRedis";
    }
}
