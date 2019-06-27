package com.example.ReactiveWeb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Deprecated
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
    @RequestMapping(value = "/callable", method = RequestMethod.GET)
    public Callable<String> executeSlowTask() {
        //System.out.println("Request received");
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("executeSlowTask call");
                return "executeSlowTask";
            }
        };
        System.out.println("Servlet thread released");

        return callable;
    }

    @RequestMapping(value = "/deferred")
    public DeferredResult<String> excuteDeferred() {
        System.out.println("Request received");
        DeferredResult<String> deferredResult = new DeferredResult<>();
        CompletableFuture.supplyAsync(()->"excuteDeferred").whenCompleteAsync((result, throwable) -> deferredResult.setResult(result));
        System.out.println("Servlet thread released");

        return deferredResult;
    }

    @Autowired
    private AsynService asynService;

    public static  AtomicLong count = new AtomicLong();
    public static  long lastTime;

    public static  AtomicLong acceptCount = new AtomicLong();


    @RequestMapping(value = "/sayHello")
    public DeferredResult<String> sayHello() {
       // System.out.println("Request received");
        DeferredResult<String> deferredResult = new DeferredResult<>();

        asynService.sayHello(deferredResult);
       // System.out.println("Servlet thread released");

        if(acceptCount.addAndGet(1)%10000==0){
            System.out.println("acceptCount:"+acceptCount);
        }

        return deferredResult;
    }

}
