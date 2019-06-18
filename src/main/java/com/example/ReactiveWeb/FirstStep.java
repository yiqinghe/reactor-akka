package com.example.ReactiveWeb;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.springframework.web.context.request.async.DeferredResult;

@Deprecated
//#greeter-messages
public class FirstStep extends AbstractActor {
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
                .match(DeferredResult.class, deferredResult -> {
                    // System.out.println("firstStep");
                    long count = Controller.count.addAndGet(1);
                    if(count%10000==0){
                        System.out.println("time:"+(System.currentTimeMillis()-Controller.lastTime)+" count:"+count);
                        Controller.lastTime = System.currentTimeMillis();
                    }
                    Request request = new Request(System.currentTimeMillis(),"uid",deferredResult);
                    AsynRpcInovker.Command command = new AsynRpcInovker.Command(request, System.currentTimeMillis(),
                            (requestContext) -> {
                                // 回调通知，将结果跟上下文传递给下一个步骤执行
                                //System.out.println("FirstStep recall");
                                secondStepActor.tell(requestContext, getSelf());
                            });
                    // 发起异步远程调用
                    AsynRpcInovker.getInstance().asynCall(command);
                })
                .build();
    }
}
