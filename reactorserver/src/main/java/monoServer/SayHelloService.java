package monoServer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import monoServer.http.HttpAsynInovker;
import monoServer.lettuce.LettuceClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.MonoSink;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Service
public class SayHelloService {

    static Map<BusinessProcess, BusinessProcess> businessProcessMap = new ConcurrentHashMap<>();

    public static final String NULL_VALUE = "@null";

    final long processGroupCount = 20;
    AtomicLong reqeustIndex = new AtomicLong();
    public static final ActorSystem  system = ActorSystem.create("root");

    public void  sayHello(MonoSink<String> deferredResult){

        long processGroupId =  reqeustIndex.addAndGet(1) % processGroupCount;

        BusinessProcess businessProcessKey = new BusinessProcess("demo", processGroupId, null, null,null,null);
        BusinessProcess businessProcess = businessProcessMap.get(businessProcessKey);
        if (businessProcess == null) {
            // 倒序组装依赖，责任链模式
            final ActorRef responseStepActor =
                    system.actorOf(ResponseStep.props(), "responseStepActor" + processGroupId);
            final ActorRef httpStepActor =
                    system.actorOf(HttpStep.props(responseStepActor), "httpStepActor" + processGroupId);
            final ActorRef redisStepActor =
                    system.actorOf(RedisStep.props(httpStepActor).withDispatcher("blocking-io-dispatcher"), "redisStepActor" + processGroupId);
            // fixme block dispatcher
            //final ActorRef redisStepActor =
              //      system.actorOf(RedisStep.props(httpStepActor), "redisStepActor" + processGroupId);
            final ActorRef firstStepActor =
                    system.actorOf(FirstStep.props(redisStepActor), "firstStepActor" + processGroupId);

            businessProcess = new BusinessProcess("demo", processGroupId, responseStepActor,httpStepActor,redisStepActor, firstStepActor);
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
        final ActorRef httpStep;
        final ActorRef redisStep;
        final ActorRef firstStepActor;

        public BusinessProcess(String business, long processGroupId, ActorRef responseStepActor,
                               ActorRef httpStep,  ActorRef redisStep,ActorRef firstStepActor) {
            this.business = business;
            this.processGroupId = processGroupId;
            this.responseStepActor = responseStepActor;
            this.httpStep = httpStep;
            this.redisStep = redisStep;
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
        static public Props props(ActorRef redisStep) {
            return Props.create(FirstStep.class, () -> new FirstStep(redisStep));
        }

        private final ActorRef redisStep;

        public FirstStep(ActorRef redisStep) {
            this.redisStep =  redisStep;
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
                                    redisStep.tell(requestContext, getSelf());
                                });
                        // 发起异步远程调用
                        AsynRpcInovker.getInstance().asynCall(command);
                    })
                    .build();
        }
    }

    /**
     * 从redis异步获取值
     */
    static class RedisStep extends AbstractActor {
        //#greeter-messages
        static public Props props(ActorRef httpActor) {
            return Props.create(RedisStep.class, () -> new RedisStep(httpActor));
        }

        private final ActorRef httpActor;

        public RedisStep(ActorRef httpActor) {
            this.httpActor = httpActor;
        }


        @Override
        public Receive createReceive() {
            System.out.println("RedisStep thread name:"+Thread.currentThread().getName()+",id:"+Thread.currentThread().getId());
            return receiveBuilder()
                    .match(Request.class, request -> {
                        LettuceClient.getInstance().get(
                                "phone2222",
                                list->{
                                    String value=null;
                                    if(list!=null && !list.isEmpty()){
                                        System.out.println("redis value:"+list.get(0));
                                        value =list.get(0);
                                       request.setPhone(value);
                                    }else{
                                        value=NULL_VALUE;
                                    }
                                    // redis获取到结果，发送到下一个处理器
                                    httpActor.tell(request, getSelf());
                                },
                                error->System.out.println(error));

                    })
                    .build();
        }
    }

    /**
     * 从http异步获取值
     */
   public static class HttpStep extends AbstractActor {
        //#greeter-messages
        static public Props props(ActorRef responseStepActor) {
            return Props.create(HttpStep.class, () -> new HttpStep(responseStepActor));
        }

        private final ActorRef responseStepActor;

        public HttpStep(ActorRef responseStepActor) {
            this.responseStepActor = responseStepActor;
        }


        @Override
        public Receive createReceive() {
            System.out.println("HttpStep thread name:"+Thread.currentThread().getName());

            return receiveBuilder()
                    .match(Request.class, request -> {

                        HttpAsynInovker.Command command = new HttpAsynInovker.Command(request, System.currentTimeMillis(),
                                (resultFromHttp) -> {
                                    // 回调通知，将结果跟上下文传递给下一个步骤执行
                                    // System.out.println("FirstStep recall");
                                    responseStepActor.tell(resultFromHttp, getSelf());
                        });
                        // 发起异步远程调用
                        HttpAsynInovker.getInstance().asynCall(command);

                    })
                    .build();
        }
    }

    /**
     * 专门最后输出给前端的步骤
     */
   public static class ResponseStep extends AbstractActor {
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
