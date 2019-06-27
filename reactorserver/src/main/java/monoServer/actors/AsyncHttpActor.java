package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import monoServer.AsynRpcInovker;
import monoServer.Request;
import monoServer.http.HttpAsynInovker;
import reactor.core.publisher.MonoSink;

public abstract class AsyncHttpActor extends BaseActor implements HttpAsynInovker.Command.CommandDoneListener{

    public AsyncHttpActor(ActorRef nextStep) {
        this.nextStep =  nextStep;
    }

    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(Object.class, param -> {
                    this.execute(param);
                })
                .build();
    }

    public abstract void execute(Object param);

    public abstract void onCommandDone(Request request);


    @Override
    public void recall(Request request) {
        nextStep.tell(request,getSelf());
    }
}
