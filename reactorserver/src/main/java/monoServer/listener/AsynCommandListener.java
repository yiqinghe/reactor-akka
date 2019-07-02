package monoServer.listener;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import monoServer.common.ActContext;

public interface AsynCommandListener {

    public void onSuccess(ActContext context, Object result);
    public void onFail(ActContext context, Throwable exception);
}
