package client;

import akka.actor.ActorRef;
import akka.actor.Props;

//#greeter-messages
public class FirstStep extends AbstractBusinessActor {
    //#greeter-messages
    static public Props props(ActorRef secondStepActor) {
        return Props.create(FirstStep.class, () -> new FirstStep(secondStepActor));
    }

    private final ActorRef secondStepActor;

    public FirstStep(ActorRef secondStepActor) {
        this.secondStepActor = secondStepActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Main.Request.class, request -> {
                    // 监控是否超过5ms。fixme 使用切面替代
                    //ActorMonitor.MonitorEntityRecall monitorEntityRecall = ActorMonitor.monitor20ms("FirstStep", request.uid,null);
                    // 超时杀掉actor
                    ActorMonitor.MonitorEntityRecall monitorEntityRecallStop = ActorMonitor.monitor50ms("FirstStep", request.uid,this.getSelf());
                    // 业务逻辑处理
                    for (int i = 0; i < 10000; i++) {
                        // Thread.sleep(1);
                    }
                    AsynRpcInovker.Command command = new AsynRpcInovker.Command(request, System.currentTimeMillis(),
                            (requestData) -> {
                                // 回调通知，将结果跟上下文传递给下一个步骤执行
                                // System.out.println("FirstStep recall");
                                secondStepActor.tell(requestData, getSelf());
                            });
                    // 发起异步远程调用
                    AsynRpcInovker.getInstance().asynCall(command);
                    // 设置完成时间
                    //monitorEntityRecall.doneTime = System.currentTimeMillis();
                    monitorEntityRecallStop.doneTime = System.currentTimeMillis();
                })
                .build();
    }
}
