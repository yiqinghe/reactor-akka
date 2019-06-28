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
import reactor.core.publisher.MonoSink;

public abstract class FristActor extends BaseActor {

    public FristActor(ActorRef nextStep) {
        this.nextStep =  nextStep;
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(MonoSink.class, deferredResult -> {
                    ActorRef atorRef = this.execute(deferredResult);

                    atorRef.tell(deferredResult,getSelf());
                })
                .build();
    }

    public abstract Object execute(MonoSink param);


}
