package monoServer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.springframework.stereotype.Service;
import reactor.core.publisher.MonoSink;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class SayHelloService {

    static Map<BusinessProcess, BusinessProcess> businessProcessMap = new ConcurrentHashMap<>();


    final long processGroupCount = 20;
    AtomicLong reqeustIndex = new AtomicLong();
    public static final ActorSystem  system = ActorSystem.create("root");

    public void  sayHello(MonoSink<String> deferredResult){

        long processGroupId =  reqeustIndex.addAndGet(1) % processGroupCount;

        BusinessProcess businessProcessKey = new BusinessProcess("demo", processGroupId, null, null);
        BusinessProcess businessProcess = businessProcessMap.get(businessProcessKey);
        if (businessProcess == null) {
            // 倒序组装依赖，责任链模式
            final ActorRef responseStepActor =
                    system.actorOf(ResponseStep.props(), "responseStepActor" + processGroupId);
            final ActorRef firstStepActor =
                    system.actorOf(FirstStep.props(responseStepActor), "firstStepActor" + processGroupId);

            businessProcess = new BusinessProcess("demo", processGroupId, responseStepActor, firstStepActor);
            businessProcessMap.put(businessProcessKey, businessProcess);
        }
        businessProcess.firstStepActor.tell(deferredResult, ActorRef.noSender());

    }

    /**
     * 定义一组业务处理执行单元
     */
    static class BusinessProcess {
        final String business;
        // 组id
        final long processGroupId;
        final ActorRef responseStepActor;
        final ActorRef firstStepActor;

        public BusinessProcess(String business, long processGroupId, ActorRef responseStepActor, ActorRef firstStepActor) {
            this.business = business;
            this.processGroupId = processGroupId;
            this.responseStepActor = responseStepActor;
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

    static class FirstStep extends AbstractActor {
        //#greeter-messages
        static public Props props(ActorRef secondStepActor) {
            return Props.create(FirstStep.class, () -> new FirstStep(secondStepActor));
        }

        private final ActorRef responseStepActor;

        public FirstStep(ActorRef responseStepActor) {
            this.responseStepActor = responseStepActor;
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(MonoSink.class, deferredResult -> {
                        // System.out.println("firstStep");
                        long count = Controller.count.addAndGet(1);
                        if(count%10000==0){
                            System.out.println("time:"+(System.currentTimeMillis()- Controller.lastTime)+" count:"+count);
                            Controller.lastTime = System.currentTimeMillis();
                        }
                        Request request = new Request(System.currentTimeMillis(),"uid",deferredResult);
                        AsynRpcInovker.Command command = new AsynRpcInovker.Command(request, System.currentTimeMillis(),
                                (requestContext) -> {
                                    // 回调通知，将结果跟上下文传递给下一个步骤执行
                                    //System.out.println("FirstStep recall");
                                    responseStepActor.tell(requestContext, getSelf());
                                });
                        // 发起异步远程调用
                        AsynRpcInovker.getInstance().asynCall(command);
                    })
                    .build();
        }
    }
    static class ResponseStep extends AbstractActor {
        //#greeter-messages
        static public Props props() {
            return Props.create(ResponseStep.class, () -> new ResponseStep());
        }


        public ResponseStep() {
        }

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                    .match(Request.class, request -> {

                        request.deferredResult.success("Hello");

                    })
                    .build();
        }
    }

}
