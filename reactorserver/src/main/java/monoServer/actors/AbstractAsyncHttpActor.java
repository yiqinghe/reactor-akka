package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import monoServer.AsynRpcInovker;
import monoServer.Request;
import monoServer.common.ActContext;
import monoServer.http.HttpAsynInovker;
import monoServer.listener.HttpCommandDoneListener;
import monoServer.request.HttpRequest;
import reactor.core.publisher.MonoSink;

public abstract class AbstractAsyncHttpActor extends BaseActor implements HttpCommandDoneListener{


    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, param -> {
                    HttpRequest request = this.execute(param);
                })
                .build();
    }

    public abstract HttpRequest execute(Object param);

    public abstract Class<ActorRef> onCommandDone(ActContext context);


    @Override
    public void onHttpCommandDone(ActContext context) {
        Class<ActorRef> actorRefClass = onCommandDone(context);

        //nextStep.tell(context,getSelf());
    }
}
