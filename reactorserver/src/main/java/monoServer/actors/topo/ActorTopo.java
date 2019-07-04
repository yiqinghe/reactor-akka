package monoServer.actors.topo;

import akka.actor.ActorRef;

import annotation.BlockActor;
import annotation.DBActor;
import monoServer.actors.AbstractDBActor;
import monoServer.actors.BaseActor;
import monoServer.actors.ResponseActor;
import monoServer.common.ActContext;
import monoServer.common.GlobalActorHolder;
import monoServer.enums.ActorGroupIdEnum;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ActorTopo {
    private static final int NOT_INITED = 0;

    private static final int INITING = 1;

    private static final int INITING_TOPO = 2;

    private static final int INITING_FRIST = 3;

    private static final int INITING_BUILD = 4;

    private static final int INITED = 5;

    private Class<? extends BaseActor> frist;

    private Set<Class<? extends BaseActor>> actorClasses;

    private ActorGroupIdEnum actorGroupIdEnum;

    private Map<Class<? extends BaseActor>,List<ActorRef>> totalActors;

    Map<Class<? extends BaseActor>,AtomicInteger> roundCounters = new HashMap<>();

    private int instanceCount = 20;

    private AtomicInteger nextServerCyclicCounter = new AtomicInteger(0);

    public int getInstanceCount() {
        return instanceCount;
    }

    public ActorGroupIdEnum getActorGroupIdEnum() {
        return actorGroupIdEnum;
    }

    public Map<Class<? extends BaseActor>, List<ActorRef>> getTotalActors() {
        return totalActors;
    }

    public Map<Class<? extends BaseActor>, AtomicInteger> getRoundCounters() {
        return roundCounters;
    }

    public static ActorTopo newBuilder(ActorGroupIdEnum actorGroupIdEnum){
        if(GlobalActorHolder.holders.get(actorGroupIdEnum) == null){
            synchronized (actorGroupIdEnum){
                if(GlobalActorHolder.holders.get(actorGroupIdEnum) == null){
                    GlobalActorHolder.holders.put(actorGroupIdEnum,new ActorTopo(actorGroupIdEnum));
                }
                return GlobalActorHolder.holders.get(actorGroupIdEnum);
            }
        }else{
            return GlobalActorHolder.holders.get(actorGroupIdEnum);
        }
    }
    private AtomicInteger initStatus = new AtomicInteger(NOT_INITED);
    private ActorTopo(ActorGroupIdEnum actorGroupIdEnum){
        this.actorGroupIdEnum = actorGroupIdEnum;
        this.initStatus.set(INITING);
    }

    public ActorTopo topo(Class<? extends BaseActor>... actorClasses) throws IllegalAccessException, InstantiationException {
        if(initStatus.get() == INITED || !initStatus.compareAndSet(INITING,INITING_TOPO)){
            return this;
        }
        if(actorClasses != null) {
            this.actorClasses = new HashSet<>(Arrays.asList(actorClasses));
        }
        return this;
    }

    public ActorTopo frist(Class<? extends BaseActor> actor){
        if(initStatus.get() == INITED || !initStatus.compareAndSet(INITING,INITING_FRIST)){
            return this;
        }
        this.frist = actor;
        return this;
    }

    public ActorTopo build(int parallNum) throws InstantiationException, IllegalAccessException {
        if(initStatus.get() == INITED || !initStatus.compareAndSet(INITING,INITING_BUILD)){
            return this;
        }
        if(frist == null){
            throw  new RuntimeException("you must assign the frist actor");
        }
        if(actorGroupIdEnum == null){
            throw new RuntimeException("you must assign the idEnum");
        }
        if(parallNum < 1){
            parallNum = instanceCount;
        }
        totalActors = new HashMap<>();
        List<ActorRef> actorList = null;
        //每个actorref实例化20个
        for (Class<? extends BaseActor> actorClass: actorClasses) {
            actorList = new ArrayList<>();
            boolean isBlock = false;
            boolean isDB = false;
            if(actorClass.getAnnotation(BlockActor.class) != null){
                isBlock = true;
            }
            if(actorClass.getAnnotation(DBActor.class) != null){
                isDB = true;
            }
            for (int i = 0; i < parallNum; i++) {
                ActorRef actorRef = null;
                if(isBlock) {
                    if(isDB){
                        actorRef = GlobalActorHolder.system.actorOf(AbstractDBActor.props(actorClass).withDispatcher("blocking-io-dispatcher")
                                , actorGroupIdEnum.getServiceId() + "_" + actorClass.getSimpleName() + "_" + i);
                    }else{
                        actorRef = GlobalActorHolder.system.actorOf(BaseActor.props(actorClass).withDispatcher("blocking-io-dispatcher")
                                , actorGroupIdEnum.getServiceId() + "_" + actorClass.getSimpleName() + "_" + i);
                    }
                }else{
                     actorRef = GlobalActorHolder.system.actorOf(BaseActor.props(actorClass)
                            , actorGroupIdEnum.getServiceId() + "_" + actorClass.getSimpleName() + "_" + i);
                }
                actorList.add(actorRef);
            }
            //RepointableActorRef
            totalActors.put(actorClass,actorList);
            roundCounters.put(actorClass,new AtomicInteger(0));
        }
        //把frist也要添加进去
        actorList = new ArrayList<>();
        for (int i = 0; i < parallNum; i++) {
            ActorRef actorRef = GlobalActorHolder.system.actorOf(BaseActor.props(frist)
                    , actorGroupIdEnum.getServiceId()+"_"+frist.getSimpleName()+"_"+i);
            actorList.add(actorRef);
        }
        totalActors.put(frist,actorList);
        roundCounters.put(frist,new AtomicInteger(0));
        //把response也要添加进去
        actorList = new ArrayList<>();
        for (int i = 0; i < parallNum; i++) {
            ActorRef actorRef = GlobalActorHolder.system.actorOf(BaseActor.props(ResponseActor.class)
                    , actorGroupIdEnum.getServiceId()+"_"+ResponseActor.class.getSimpleName()+"_"+i);
            actorList.add(actorRef);
        }
        totalActors.put(ResponseActor.class,actorList);

        GlobalActorHolder.holders.putIfAbsent(actorGroupIdEnum, this);
        initStatus.set(INITED);
        return this;
    }

    public Mono<String> start(Map<String, Object> params){

        Mono<String> monoResult = Mono.create(deferredResult->{

            ActContext context = new ActContext();
            context.setRequestData(params);
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
