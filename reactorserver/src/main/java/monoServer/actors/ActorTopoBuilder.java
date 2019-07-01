package monoServer.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.RepointableActorRef;
import monoServer.SayHelloService;
import monoServer.common.ActContext;
import monoServer.common.GlobalActorHolder;
import monoServer.enums.ActorGroupIdEnum;
import org.apache.commons.lang.ClassUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ActorTopoBuilder {

    private Class<? extends BaseActor> frist;

    private Class<? extends BaseActor>[] actorClasses;

    private ActorGroupIdEnum actorGroupIdEnum;

    private Map<Class<? extends BaseActor>,List<ActorRef>> totalActors;

    private ActorTopo actorTopo;

    public ActorTopoBuilder topo(Class<? extends BaseActor>... actorClasses) throws IllegalAccessException, InstantiationException {
        if(this.actorTopo != null){
            return this;
        }
        this.actorClasses = actorClasses;

        return this;
    }

    public ActorTopoBuilder frist(Class<? extends BaseActor> actor){
        if(this.actorTopo != null){
            return this;
        }
        this.frist = actor;
        return this;
    }

    public ActorTopo build(Map<String,Object> params) throws InstantiationException, IllegalAccessException {
        if(this.actorTopo != null){
            return actorTopo;
        }
        if(frist == null){
            throw  new RuntimeException("you must assign the frist actor");
        }
        if(actorGroupIdEnum == null){
            throw new RuntimeException("you must assign the idEnum");
        }
        totalActors = new HashMap<>();
        List<ActorRef> actorList = null;
        //每个actorref实例化20个
        for (Class<? extends BaseActor> actorClass: actorClasses) {
            actorList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                ActorRef actorRef = GlobalActorHolder.system.actorOf(BaseActor.props(actorClass), actorGroupIdEnum.getServiceName() + "_" + actorGroupIdEnum.getServiceId());
                actorList.add(actorRef);
            }
            //RepointableActorRef
            totalActors.put(actorClass,actorList);
        }
        //把frist也要添加进去
        actorList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            ActorRef actorRef = GlobalActorHolder.system.actorOf(BaseActor.props(frist), actorGroupIdEnum.getServiceName() + "_" + actorGroupIdEnum.getServiceId());
            actorList.add(actorRef);
        }
        totalActors.put(frist,actorList);

        actorTopo = new ActorTopo(frist,actorClasses,params,actorGroupIdEnum,totalActors);
        GlobalActorHolder.holders.put(actorGroupIdEnum,actorTopo);
        return actorTopo;
    }


    public ActorTopoBuilder setServiceId(ActorGroupIdEnum actorGroupIdEnum){
        this.actorGroupIdEnum = actorGroupIdEnum;
        this.actorTopo=GlobalActorHolder.holders.get(actorGroupIdEnum);
        return this;
    }

    public static ActorTopo getActorTopoById(ActorGroupIdEnum actorGroupIdEnum){
        return  GlobalActorHolder.holders.get(actorGroupIdEnum);
    }


    public class ActorTopo {

        private Class<? extends BaseActor> frist;

        private Class<? extends BaseActor>[] actorClasses;

        private Map<String,Object> params;

        private Map<Class<? extends BaseActor>,List<ActorRef>> totalActors;

        private ActorGroupIdEnum actorGroupIdEnum;

        private AtomicInteger nextServerCyclicCounter;

        public Map<Class<? extends BaseActor>, List<ActorRef>> getTotalActors() {
            return totalActors;
        }

        public void setTotalActors(Map<Class<? extends BaseActor>, List<ActorRef>> totalActors) {
            this.totalActors = totalActors;
        }

        public ActorTopo(Class<? extends BaseActor> frist, Class<? extends BaseActor>[] actorClasses, Map<String, Object> params
                , ActorGroupIdEnum actorGroupIdEnum, Map<Class<? extends BaseActor>,List<ActorRef>> totalActors) {
            this.frist = frist;
            this.actorClasses = actorClasses;
            this.params = params;
            this.totalActors = totalActors;
            this.actorGroupIdEnum = actorGroupIdEnum;
            this.nextServerCyclicCounter = new AtomicInteger(0);
        }

        public Mono<String> start(){

            Mono<String> monoResult = Mono.create(deferredResult->{

                ActContext context = new ActContext();
                context.setBusinessData(params);
                context.setMonoSink(deferredResult);
                context.setActorGroupIdEnum(actorGroupIdEnum);
                totalActors.get(frist).get(incrementAndGetModulo(20)).tell(context,ActorRef.noSender());
            });
            return monoResult;
        }

        private int incrementAndGetModulo(int modulo) {
            for (;;) {
                int current = nextServerCyclicCounter.get();
                int next = (current + 1) % modulo;
                if (nextServerCyclicCounter.compareAndSet(current, next))
                    return next;
            }
        }

    }

    public static void main(String[] args) {
        final ActorRef responseStepActor =
                GlobalActorHolder.system.actorOf(SayHelloService.ResponseStep.props(), "responseStepActor" + 1);
        final ActorRef httpStepActor =
                GlobalActorHolder.system.actorOf(SayHelloService.HttpStep.props(responseStepActor), "httpStepActor" + 1);
    }
}
