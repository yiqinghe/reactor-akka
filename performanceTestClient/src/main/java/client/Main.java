package client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class Main {
    // 请求总数
    static long sumCount = 10000;
    public static int setConnectTimeout = 1000;
    public static int setSocketTimeout = 1000;
    public static int setConnectionRequestTimeout = 1000;
    public static int setMaxTotal = 5000;
    public static int setDefaultMaxPerRoute = 5000;

    // 预计qps
    public static int rate = 10000;

    public volatile static AtomicLong doneSize = new AtomicLong();
    public static long startTime;
    final static public Object lock = new Object();

    public static Map<Request, Request> requests;
    public static Queue<Request> doneQueue = new ArrayBlockingQueue<>(100000);

    public final static ActorSystem system = ActorSystem.create("root");

    public static void main(String[] args) {
        // 处理参数
        processParameters(args);

        // 启动模拟响应客户端的io线程
        CountThread countThread = new CountThread();
        countThread.start();
        // BatchService 启动批量请求
        SpringApplication.run(Main.class, args);

    }


    static class CountThread extends Thread{
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
    }

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