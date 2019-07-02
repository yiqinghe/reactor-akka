package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.FI;
import monoServer.AsynRpcInovker;
import monoServer.AsynRpcInovker.Command;
import monoServer.Controller;
import monoServer.Request;
import monoServer.SayHelloService;
import monoServer.common.ActContext;
import monoServer.common.GlobalActorHolder;
import reactor.core.publisher.MonoSink;

public abstract class AbstractCommonActor extends BaseActor {

    @Override
    public  Object buildExecuteData(ActContext context){
        return null;
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, context -> {
                    Class<? extends BaseActor> actorRefClass = this.executeAndNext(context,null);
                    dispatch(actorRefClass,context);
                })
                .build();
    }

}
