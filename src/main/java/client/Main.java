package client;

import Mono.ReactiveWebApplication;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Main extends Thread {
    // 请求总数
    static long sumCount = 100;
    public static int setConnectTimeout = 100;
    public static int setSocketTimeout = 100;
    public static int setConnectionRequestTimeout = 100;
    public static int setMaxTotal = 50;
    public static int setDefaultMaxPerRoute = 50;

    // 预计qps
    public static int rate = 5000;

    public volatile static AtomicLong doneSize = new AtomicLong();
    public static long startTime;
    final static public Object lock = new Object();

    public static Map<Request, Request> requests;
    public static Queue<Request> doneQueue = new ArrayBlockingQueue<>(100000);

    static public class Request {

        public final long requestTime;
        public final String uid;
        public final ContextData contextData;

        public Request(long requestTime, String uid, ContextData contextData) {
            this.requestTime = requestTime;
            this.uid = uid;
            this.contextData = contextData;
        }
    }

    // 模拟异步返回客户端线程
    public void run() {
        while (true) {
            try {
                synchronized (lock) {
                    lock.wait(100);
                    System.out.println("wait up size:" + doneQueue.size());
                    while (doneQueue.size() > 0) {
                        // 模拟返回终端数据
                        Request request = doneQueue.poll();
                        requests.remove(request);
                    }
                    // 所有压测模拟的请求的发完了，计算qps
                    if (requests != null && requests.isEmpty()) {
                        long cost = System.currentTimeMillis() - startTime;
                        System.out.println("all cost:" + cost);
                        if (cost != 0) {
                            System.out.println("all Request Done qps:" + (sumCount * 1000 / cost));
                        } else {
                            System.out.println("all Request Done qps");
                        }

                        Thread.sleep(1000000000);
                    }

                    System.out.println("requests size:" + (requests == null ? "null" : requests.size()) + ",doneSize:" + doneSize);
                }
                // fixme 实际不用sleep，靠notify唤醒，这句只是为了测1秒大概多大qps
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 定义一组业务处理执行单元
     */
    static class BusinessProcess {
        final String business;
        // 组id
        final int processGroupId;
        final ActorRef secondStepActor;
        final ActorRef firstStepActor;

        public BusinessProcess(String business, int processGroupId, ActorRef secondStepActor, ActorRef firstStepActor) {
            this.business = business;
            this.processGroupId = processGroupId;
            this.secondStepActor = secondStepActor;
            this.firstStepActor = firstStepActor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BusinessProcess that = (BusinessProcess) o;
            return processGroupId == that.processGroupId &&
                    Objects.equals(business, that.business);
        }

        @Override
        public int hashCode() {
            // fixme
            return Objects.hash(business, processGroupId);
        }
    }

    static Map<BusinessProcess, BusinessProcess> businessProcessMap = new ConcurrentHashMap<>();


    public static void main(String[] args) {
        // 处理参数
        processParameters(args);

        // 启动模拟响应客户端的io线程
        Main main = new Main();
        main.start();

        // 创建根actor,比较耗时的初始化时间2-3秒
        long rootInitStartTime = System.currentTimeMillis();
        System.out.println("root init time:" + (System.currentTimeMillis() - rootInitStartTime));
        try {
            startTime = System.currentTimeMillis();

            // 业务执行组个数
            int processGroupCount = 20;

            for (int i = 0; i < sumCount; i++) {
                // 如果只有一组actor执行所有请求，吞吐量上不来
                // 设置多组actor，可以提高吞吐量，对cpu占用也没有明显提高
                // 禁止每一个请求都生成一组actor来执行，actor资源是不会被gc回收，除非显示关闭。
                int processGroupId = i % processGroupCount;
                BusinessProcess businessProcessKey = new BusinessProcess("demo", processGroupId, null, null);
                BusinessProcess businessProcess = businessProcessMap.get(businessProcessKey);
                if (businessProcess == null) {
                    // 倒序组装依赖，责任链模式
                    final ActorRef secondStepActor =
                            ReactiveWebApplication.system.actorOf(SecondStep.props(), "secondStepActor" + processGroupId);
                    final ActorRef firstStepActor =
                            ReactiveWebApplication.system.actorOf(FirstStep.props(secondStepActor), "firstStepActor" + processGroupId);
                    businessProcess = new BusinessProcess("demo", processGroupId, secondStepActor, firstStepActor);
                    businessProcessMap.put(businessProcessKey, businessProcess);
                }
                // 模拟请求
                Request request = new Request(System.currentTimeMillis(), "1", new ContextData());
                if (requests == null) {
                    requests = new ConcurrentHashMap<>();
                }
                requests.put(request, request);
                businessProcess.firstStepActor.tell(request, ActorRef.noSender());


                // 瞬间发送100万请求后，持续的压测，均匀的产生模拟请求，避免一下发送太多数据
                if (i > 1000000) {
                    if (i % rate == 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            System.out.println("start finish");
//            try {
//                // fixme 如果在control调用，actor会dead？会被回收调？
//
//                Thread.sleep(100000000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }
    private static void processParameters(String[] args) {
        if(args != null){
            for (String arg : args) {
                String[] argsStr = arg.split("=");

                if (argsStr.length == 2) {
                    if ("sumCount".equals(argsStr[0])) {
                        sumCount = Long.valueOf(argsStr[1]);
                    }if ("setConnectionRequestTimeout".equals(argsStr[0])) {
                        setConnectionRequestTimeout = Integer.valueOf(argsStr[1]);
                    }if ("setConnectTimeout".equals(argsStr[0])) {
                        setConnectTimeout = Integer.valueOf(argsStr[1]);
                    }if ("setSocketTimeout".equals(argsStr[0])) {
                        setSocketTimeout = Integer.valueOf(argsStr[1]);
                    }if ("setMaxTotal".equals(argsStr[0])) {
                        setMaxTotal = Integer.valueOf(argsStr[1]);
                    }if ("setDefaultMaxPerRoute".equals(argsStr[0])) {
                        setDefaultMaxPerRoute = Integer.valueOf(argsStr[1]);
                    }if ("rate".equals(argsStr[0])) {
                        rate = Integer.valueOf(argsStr[1]);
                    }

                }
            }
        }
    }
}