package client;

import akka.actor.ActorRef;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BatchService {
    @PostConstruct
     public void batchRequest() {
        // 创建根actor,比较耗时的初始化时间2-3秒

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long rootInitStartTime = System.currentTimeMillis();
        System.out.println("root init time:" + (System.currentTimeMillis() - rootInitStartTime));
        try {
            Main.startTime = System.currentTimeMillis();

            // 业务执行组个数
            int processGroupCount = 20;

            for (int i = 0; i < Main.sumCount; i++) {
                // 如果只有一组actor执行所有请求，吞吐量上不来
                // 设置多组actor，可以提高吞吐量，对cpu占用也没有明显提高
                // 禁止每一个请求都生成一组actor来执行，actor资源是不会被gc回收，除非显示关闭。
                int processGroupId = i % processGroupCount;
                Main.BusinessProcess businessProcessKey = new Main.BusinessProcess("demo", processGroupId, null, null);
                Main.BusinessProcess businessProcess = Main.businessProcessMap.get(businessProcessKey);
                if (businessProcess == null) {
                    // 倒序组装依赖，责任链模式
                    final ActorRef secondStepActor =
                            Main.system.actorOf(SecondStep.props(), "secondStepActor" + processGroupId);
                    final ActorRef firstStepActor =
                            Main.system.actorOf(FirstStep.props(secondStepActor), "firstStepActor" + processGroupId);
                    businessProcess = new Main.BusinessProcess("demo", processGroupId, secondStepActor, firstStepActor);
                    Main.businessProcessMap.put(businessProcessKey, businessProcess);
                }
                // 模拟请求
                Main.Request request = new Main.Request(System.currentTimeMillis(), "1", new ContextData());
                if (Main.requests == null) {
                    Main.requests = new ConcurrentHashMap<>();
                }
                Main.requests.put(request, request);
                businessProcess.firstStepActor.tell(request, ActorRef.noSender());


                // 瞬间发送100万请求后，持续的压测，均匀的产生模拟请求，避免一下发送太多数据
                if (i > 1000000) {
                    if (i % Main.rate == 0) {
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
}
