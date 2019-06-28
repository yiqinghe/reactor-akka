package monoServer.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import monoServer.common.ActContext;
import monoServer.common.GlobalActorHolder;
import monoServer.enums.ActorGroupIdEnum;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActorTopoBuilder {

    private ActorRef frist;

    private Map<Class<BaseActor>,List<BaseActor>> actors;

    private ActorGroupIdEnum actorGroupIdEnum;

    private ActorTopo actorTopo;

    public ActorTopoBuilder topo(Class<? extends BaseActor>... actors){
        if(this.actorTopo != null){
            return this;
        }
        this.actors = new HashMap();
        for (Class actor:actors
             ) {

            for (int i = 0; i < 20; i++) {
                //GlobalActorHolder.system.actorOf(Props.create())
            }
        }

        return this;
    }

    public ActorTopoBuilder frist(Class<? extends BaseActor>... actor){
        if(this.actorTopo != null){
            return this;
        }
        return this;
    }

    public ActorTopo build(Map<String,Object> params){
        if(this.actorTopo != null){
            return actorTopo;
        }
        actorTopo = new ActorTopo(frist,actors,actorGroupIdEnum,params);
        return actorTopo;
    }

    public ActorTopoBuilder setServiceId(ActorGroupIdEnum actorGroupIdEnum){
        this.actorTopo=GlobalActorHolder.holders.get(actorGroupIdEnum);
        return this;
    }


    public class ActorTopo {

        private ActorRef frist;

        private Map<Class<BaseActor>,List<BaseActor>> actors;

        private ActorGroupIdEnum actorGroupIdEnum;

        private Map<String,Object> params;

        public ActorTopo(ActorRef frist, Map<Class<BaseActor>, List<BaseActor>> actors, ActorGroupIdEnum actorGroupIdEnum, Map<String, Object> params) {
            this.frist = frist;
            this.actors = actors;
            this.actorGroupIdEnum = actorGroupIdEnum;
            this.params=params;
        }

        public Mono<String> start(){
            Mono<String> monoResult = Mono.create(deferredResult->{

                ActContext context = new ActContext();
                context.setBusinessData(params);
                context.setMonoSink(deferredResult);
                frist.tell(context,ActorRef.noSender());
            });
            return monoResult;
        }
    }
}
