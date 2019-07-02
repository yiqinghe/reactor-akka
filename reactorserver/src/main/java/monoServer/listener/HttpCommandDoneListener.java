package monoServer.listener;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import monoServer.common.ActContext;

public interface HttpCommandDoneListener {

    public void onHttpCommandDone(ActContext context,String httpResult);
}
