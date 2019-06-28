package monoServer.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.Map;

public abstract class BaseActor extends AbstractActor{

    //#greeter-messages
    public Props props() {
        return Props.create(AbstractFristActor.class, this);
    }

    public void setTopo(Map<Class<ActorRef>,ActorRef> actorMap){

    }
}
