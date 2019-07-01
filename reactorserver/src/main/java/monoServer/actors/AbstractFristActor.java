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

public abstract class AbstractFristActor extends BaseActor {


    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, context -> {
                    Class<? extends BaseActor> actorRefClass = this.execute(context);
                    dispatch(actorRefClass,context);
                })
                .build();
    }

    public abstract Class<? extends BaseActor> execute(ActContext context);


}
