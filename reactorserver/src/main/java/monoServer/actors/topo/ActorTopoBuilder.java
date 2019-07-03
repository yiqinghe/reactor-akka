package monoServer.actors.topo;

import akka.actor.ActorRef;

import annotation.BlockActor;
import annotation.DBActor;
import monoServer.actorImpl.SyncDBActor;
import monoServer.actors.AbstractDBActor;
import monoServer.actors.BaseActor;
import monoServer.actors.ResponseActor;
import monoServer.common.GlobalActorHolder;
import monoServer.enums.ActorGroupIdEnum;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ActorTopoBuilder {

    private Class<? extends BaseActor> frist;

    private Set<Class<? extends BaseActor>> actorClasses;

    private ActorGroupIdEnum actorGroupIdEnum;

    private Map<Class<? extends BaseActor>,List<ActorRef>> totalActors;

    private ActorTopo actorTopo;

    private int DefaultInstanceCount = 20;

    public static ActorTopoBuilder newBuilder(ActorGroupIdEnum actorGroupIdEnum){
        return new ActorTopoBuilder(actorGroupIdEnum);
    }

    private ActorTopoBuilder(ActorGroupIdEnum actorGroupIdEnum){
        this.actorGroupIdEnum = actorGroupIdEnum;
        this.actorTopo = GlobalActorHolder.holders.get(actorGroupIdEnum);
    }

    public ActorTopoBuilder topo(Class<? extends BaseActor>... actorClasses) throws IllegalAccessException, InstantiationException {
        if(this.actorTopo != null){
            return this;
        }
        if(actorClasses != null) {
            this.actorClasses = new HashSet<>(Arrays.asList(actorClasses));
        }
        return this;
    }

    public ActorTopoBuilder frist(Class<? extends BaseActor> actor){
        if(this.actorTopo != null){

            return this;
        }
        this.frist = actor;
        return this;
    }

    public ActorTopo build(int parallNum) throws InstantiationException, IllegalAccessException {
        if(this.actorTopo != null){
            return actorTopo;
        }
        if(frist == null){
            throw  new RuntimeException("you must assign the frist actor");
        }
        if(actorGroupIdEnum == null){
            throw new RuntimeException("you must assign the idEnum");
        }
        if(parallNum < 1){
            parallNum = DefaultInstanceCount;
        }
        totalActors = new HashMap<>();
        List<ActorRef> actorList = null;
        Map<Class<? extends BaseActor>,AtomicInteger> roundCounters = new HashMap<>();
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
        if( GlobalActorHolder.holders.get(actorGroupIdEnum) == null){
            synchronized (actorGroupIdEnum){
                if(GlobalActorHolder.holders.get(actorGroupIdEnum) == null) {
                    actorTopo = new ActorTopo(frist, actorGroupIdEnum, totalActors, parallNum, roundCounters);
                    GlobalActorHolder.holders.put(actorGroupIdEnum,actorTopo);
                }
            }
        }
        return actorTopo;
    }



}
