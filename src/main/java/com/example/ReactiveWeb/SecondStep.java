package com.example.ReactiveWeb;

import akka.actor.AbstractActor;
import akka.actor.Props;
@Deprecated
//#greeter-messages
public class SecondStep extends AbstractActor {
    //#greeter-messages
    static public Props props() {
        return Props.create(SecondStep.class, () -> new SecondStep());
    }


    public SecondStep() {
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Request.class, request -> {
                    // 监控是否超过5ms。fixme 使用切面替代
                   // ActorMonitor.MonitorEntityRecall monitorEntityRecall = ActorMonitor.monitor5ms("FirstStep", request.uid);
//                    // 业务逻辑处理
//                    for (int i = 0; i < 1; i++) {
//                        // 模拟阻塞
//                        Thread.sleep(1);
//                    }
//                    // 完成整个业务调用
//                    // System.out.println("ResponseStep done,size:"+();
//                    request.contextData.response = "Success";
//                    Main.doneSize.addAndGet(1);
                    // 唤醒
//                    synchronized (Main.lock) {
//                        Main.doneQueue.offer(request);
//                        Main.lock.notifyAll();
//                    }
                    // 设置完成时间
                    request.deferredResult.setResult("Hello");

                })
                .build();
    }
}
