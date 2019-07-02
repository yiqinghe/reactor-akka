package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import monoServer.AsynRpcInovker;
import monoServer.Request;
import monoServer.common.ActContext;
import monoServer.http.HttpAsynInovker;
import monoServer.http.HttpAsyncClient;
import monoServer.listener.HttpCommandDoneListener;
import monoServer.request.HttpRequest;
import reactor.core.publisher.MonoSink;

public abstract class AbstractAsyncHttpActor extends BaseActor implements HttpCommandDoneListener{


    @Override
    public Receive createReceive() {

        return receiveBuilder()
                .match(ActContext.class, context -> {
                    HttpRequest request = this.execute(context);
                    //多次http调用 只会纪录上一次的信息
                    context.setHttpRequest(request);
                    HttpAsyncClient.Command clientCommand = new HttpAsyncClient.Command(context,System.currentTimeMillis(),this);
                    HttpAsyncClient.getInstance().asynCall(clientCommand);
                })
                .build();
    }

    public abstract HttpRequest execute(ActContext context);

    public abstract Class<? extends BaseActor> onCommandDone(ActContext context);


    @Override
    public void onHttpCommandDone(ActContext context) {
        Class<? extends BaseActor> actorRefClass = onCommandDone(context);
        dispatch(actorRefClass,context);
    }
}
